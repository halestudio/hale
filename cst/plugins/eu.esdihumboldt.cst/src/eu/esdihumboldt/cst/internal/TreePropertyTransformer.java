/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.ContextMatcher;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.matcher.AsDeepAsPossible;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.DuplicationVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.InstanceVisitor;
import eu.esdihumboldt.hale.common.align.transformation.function.FamilyInstance;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Property transformer based on a {@link TransformationTree}.
 * @author Simon Templer
 */
public class TreePropertyTransformer implements PropertyTransformer {
	
//	private final TransformationReporter reporter;

	private final InstanceSink sink;
	
	private final TransformationTreePool treePool;
	
	private final FunctionExecutor executor;
	
	private final InstanceBuilder builder;
	
	private final ExecutorService executorService;

	/**
	 * Create a simple property transformer
	 * @param alignment the alignment
	 * @param reporter the transformation log to report any transformation 
	 *   messages to
	 * @param sink the target instance sink
	 * @param engines the transformation engine manager
	 */
	public TreePropertyTransformer(Alignment alignment, 
			TransformationReporter reporter, InstanceSink sink, 
			EngineManager engines) {
//		this.reporter = reporter;
		this.sink = sink;
		
		ContextMatcher matcher = new AsDeepAsPossible(); //XXX how to determine matcher?
		treePool = new TransformationTreePool(alignment, matcher);
		executor = new FunctionExecutor(reporter, engines);
		builder = new InstanceBuilder();
		
		executorService = Executors.newFixedThreadPool(4);
	}

	/**
	 * @see PropertyTransformer#publish(FamilyInstance, MutableInstance)
	 */
	@Override
	public void publish(final FamilyInstance source, final MutableInstance target) {
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				// identify transformations to be executed on given instances
				// create/get a transformation tree
				TransformationTree tree = treePool.getTree(target.getDefinition());
				
				// apply instance value to transformation tree
				InstanceVisitor instanceVisitor = new InstanceVisitor(source, tree);
				tree.accept(instanceVisitor);
				
				// duplicate subtree as necessary
				DuplicationVisitor duplicationVisitor = new DuplicationVisitor();
				tree.accept(duplicationVisitor);
				
				// apply functions
				tree.accept(executor);
				
				// fill instance
				builder.populate(target, tree);

				//XXX ok to add to sink in any thread?!
				//XXX addInstance and close were made synchronized in OrientInstanceSink
				//XXX instead collect instances and write them in only one thread?
				// after property transformations, publish target instance
				sink.addInstance(target);
				
				// and release the tree for further use
				treePool.releaseTree(tree);
			}
		});
	}
	
	@Override
	public void join(boolean cancel) {
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
			//TODO make configurable?
			if (!executorService.awaitTermination(15, TimeUnit.MINUTES)) {
				//TODO error message
			}
		} catch (InterruptedException e) {
			// ignore
		}
	}

}
