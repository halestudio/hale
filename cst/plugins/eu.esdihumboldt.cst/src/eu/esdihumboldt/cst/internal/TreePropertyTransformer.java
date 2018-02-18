/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import eu.esdihumboldt.cst.extension.hooks.HooksUtil;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook.TreeState;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHooks;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.ContextMatcher;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.matcher.AsDeepAsPossible;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.DuplicationVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.InstanceVisitor;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.report.SimpleLogContext;
import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataWorker;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceMetadata;
import eu.esdihumboldt.hale.common.instance.model.InstanceUtil;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntProcedure;

/**
 * Property transformer based on a {@link TransformationTree}.
 * 
 * @author Simon Templer
 */
public class TreePropertyTransformer implements PropertyTransformer {

//	private final TransformationReporter reporter;

	private final InstanceSink sink;

	private final TransformationTreePool treePool;

	private final List<FunctionExecutor> executors;

	private final InstanceBuilder builder;

	private final ExecutorService executorService;

	/**
	 * Controls if multiple threads are used for transformation.
	 */
	private final boolean forkedTransformation = false;

	// make metadataworker threadsave
	private final ThreadLocal<MetadataWorker> metaworkerthread = new ThreadLocal<MetadataWorker>() {

		@Override
		protected MetadataWorker initialValue() {
			return new MetadataWorker();
		}
	};

	private final TransformationTreeHooks treeHooks;

	private final TObjectIntHashMap<Cell> instanceCounter = new TObjectIntHashMap<>();

	private final TransformationReporter reporter;

	/**
	 * Create a simple property transformer
	 * 
	 * @param alignment the alignment
	 * @param reporter the transformation log to report any transformation
	 *            messages to
	 * @param sink the target instance sink
	 * @param engines the transformation engine manager
	 * @param context the transformation execution context
	 */
	public TreePropertyTransformer(Alignment alignment, TransformationReporter reporter,
			InstanceSink sink, EngineManager engines, TransformationContext context) {
		this.reporter = reporter;
		this.sink = sink;

		// XXX how to determine matcher?
		ContextMatcher matcher = new AsDeepAsPossible(context.getServiceProvider());
		treePool = new TransformationTreePool(alignment, matcher);

		/*
		 * create executors in order of priority, highest first.
		 */
		executors = new ArrayList<FunctionExecutor>();
		Priority[] priorityValuesDescending = Priority.values();
		for (Priority priority : priorityValuesDescending) {
			FunctionExecutor executor = new FunctionExecutor(reporter, engines, context, priority);
			executors.add(executor);
		}
		builder = new InstanceBuilder();

		treeHooks = HalePlatform.getService(TransformationTreeHooks.class);

		if (forkedTransformation) {
			executorService = new ThreadPoolExecutor(4, 4, // 4 threads
					0L, TimeUnit.MILLISECONDS,
					// maximum queue size 1000 (keep 1000 instances/workers in
					// memory
					// simultaneously at max)
					new LinkedBlockingQueue<Runnable>(1000) {

						private static final long serialVersionUID = 1L;

						@Override
						public boolean offer(Runnable e) {
							// wait for space to be free in the queue
							try {
								super.put(e);
							} catch (InterruptedException e1) {
								// XXX correct to return false?
								return false;
							}
							// then accept
							return true;

							/*
							 * Alternative could be calling offer in a loop with
							 * a wait until it returns true.
							 */
						}

					});
		}
		else {
			executorService = null;
		}
	}

	/**
	 * @see PropertyTransformer#publish(FamilyInstance, MutableInstance,
	 *      TransformationLog, Cell)
	 */
	@Override
	public void publish(final FamilyInstance source, final MutableInstance target,
			final TransformationLog typeLog, final Cell typeCell) {
		instanceCounter.adjustOrPutValue(typeCell, 1, 1);

		// increase output type counter
		reporter.stats().at("createdPerType").at(target.getDefinition().getName().toString())
				.next();

		Runnable job = new Runnable() {

			@Override
			public void run() {
				try {
					SimpleLogContext.withLog(typeLog, () -> {

						// Add the meta data ID of the source as SourceID to the
						// target
						Collection<Instance> sources = InstanceUtil.getInstanceOutOfFamily(source);
						Set<Object> ids = new HashSet<Object>();
						for (Instance inst : sources) {
							// Merge instances may have multiple IDs
							List<Object> sourceIDs = inst.getMetaData(InstanceMetadata.METADATA_ID);
							if (sourceIDs != null) {
								ids.addAll(sourceIDs);
							}
						}
						InstanceMetadata.setSourceID(target, ids.toArray());

						// identify transformations to be executed on given
						// instances
						// create/get a transformation tree
						TransformationTree tree = treePool.getTree(typeCell);

						// State: base tree
						HooksUtil.executeTreeHooks(treeHooks, TreeState.MINIMAL, tree, target);

						// apply instance value to transformation tree
						InstanceVisitor instanceVisitor = new InstanceVisitor(source, tree,
								typeLog);
						tree.accept(instanceVisitor);

						// State: basic source populated tree

						// duplicate subtree as necessary
						DuplicationVisitor duplicationVisitor = new DuplicationVisitor(tree,
								typeLog);
						tree.accept(duplicationVisitor);
						duplicationVisitor.doAugmentationTrackback();

						// State: source populated tree (duplication complete)
						HooksUtil.executeTreeHooks(treeHooks, TreeState.SOURCE_POPULATED, tree,
								target);

						// apply functions
						for (FunctionExecutor functionExecutor : executors) {
							functionExecutor.setTypeCell(typeCell);
							tree.accept(functionExecutor);
						}

						// State: full tree (target populated)
						HooksUtil.executeTreeHooks(treeHooks, TreeState.FULL, tree, target);

						// fill instance
						builder.populate(target, tree, typeLog);

						// generate the rest of the metadatas
						metaworkerthread.get().generate(target);

						// XXX ok to add to sink in any thread?!
						// XXX addInstance and close were made synchronized in
						// OrientInstanceSink
						// XXX instead collect instances and write them in only
						// one
						// thread?
						// after property transformations, publish target
						// instance
						sink.addInstance(target);

						// and release the tree for further use
						treePool.releaseTree(tree);
					});
				} catch (Throwable e) {
					/*
					 * Catch any error, as exceptions in the executor service
					 * will only result in a message on the console.
					 */
					typeLog.error(
							typeLog.createMessage("Error performing property transformations", e));
				}
			}
		};

		if (executorService != null) {
			executorService.execute(job);
		}
		else {
			job.run();
		}
	}

	@Override
	public void join(boolean cancel) {
		if (executorService != null) {
			if (cancel) {
				executorService.shutdownNow();
			}
			else {
				executorService.shutdown();
			}

			if (executorService.isTerminated()) {
				return;
			}
			try {
				// TODO make configurable?
				if (!executorService.awaitTermination(15, TimeUnit.MINUTES)) {
					// TODO error message
				}
			} catch (InterruptedException e) {
				// ignore
			}
		}

		// report instance counts
		instanceCounter.forEachEntry(new TObjectIntProcedure<Cell>() {

			@Override
			public boolean execute(Cell cell, int count) {
				reporter.info(new TransformationMessageImpl(cell,
						MessageFormat.format("Created {0} instances during transformation", count),
						null));

				// also store as statistics
				reporter.stats().at("createdPerCell").at(cell.getId()).set(count);

				return true;
			}
		});
	}

}
