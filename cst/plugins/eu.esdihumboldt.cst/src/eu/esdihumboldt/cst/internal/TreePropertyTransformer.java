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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.ContextMatcher;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.matcher.AsDeepAsPossible;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.DuplicationVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.InstanceVisitor;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceMetadata;
import eu.esdihumboldt.hale.common.instance.model.InstanceUtil;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.ui.ttreeexporter.TTreeExporter;

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
		
		executorService = Executors.newFixedThreadPool(1);
	}

	/**
	 * @see PropertyTransformer#publish(FamilyInstance, MutableInstance, TransformationLog)
	 */
	@Override
	public void publish(final FamilyInstance source, 
			final MutableInstance target, final TransformationLog typeLog) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					//Add the meta data ID of the source as SourceID to the target
					Collection<Instance> sources = InstanceUtil.getInstanceOutOfFamily(source);
					Set<Object> ids = new HashSet<Object>();
				    for(Instance inst : sources){
				    	List<Object> sourceIDs = inst.getMetaData(InstanceMetadata.METADATA_ID); // Merge instances may have multiple IDs
				    	if (sourceIDs != null) {
				    		ids.addAll(sourceIDs);
				    	}
			        }					
				    InstanceMetadata.setSourceID(target, ids.toArray());
					
					// identify transformations to be executed on given instances
					// create/get a transformation tree
					TransformationTree tree = treePool.getTree(target.getDefinition());
					TTreeExporter.exportTTree(tree);
					
					// apply instance value to transformation tree
					InstanceVisitor instanceVisitor = new InstanceVisitor(source, tree);
					tree.accept(instanceVisitor);
					TTreeExporter.exportTTree(tree);
					
					// duplicate subtree as necessary
					DuplicationVisitor duplicationVisitor = new DuplicationVisitor(tree);
					tree.accept(duplicationVisitor);
					TTreeExporter.exportTTree(tree);
					duplicationVisitor.doAugmentationTrackback();
					TTreeExporter.exportTTree(tree);
					
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
				} catch (Throwable e) {
					/*
					 * Catch any error, as exceptions in the executor service
					 * will only result in a message on the console.
					 */
					typeLog.error(typeLog.createMessage("Error performing property transformations", e));
				}
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
