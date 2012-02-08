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

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.ContextMatcher;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.matcher.AsDeepAsPossible;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.InstanceVisitor;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.instance.model.Instance;
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
		treePool = new TransformationTreePool(alignment, matcher );
		executor = new FunctionExecutor(reporter, engines);
		builder = new InstanceBuilder();
	}

	/**
	 * @see PropertyTransformer#publish(Collection, Instance, MutableInstance)
	 */
	@Override
	public void publish(Collection<? extends Type> sourceTypes,
			Instance source, MutableInstance target) {
		//TODO do actual transformation in a job or worker thread
		
		// identify transformations to be executed on given instances
		// create/get a transformation tree
		TransformationTree tree = treePool.getTree(target.getDefinition());
		
		// apply instance values to transformation tree
		InstanceVisitor instanceVisitor = new InstanceVisitor(source);
		tree.accept(instanceVisitor);
		
		// apply functions
		tree.accept(executor);
		
		// fill instance
		builder.populate(target, tree);
		
		// after property transformations, publish target instance
		sink.addInstance(target);
		// and release the tree for further use
		treePool.releaseTree(tree);
	}

}
