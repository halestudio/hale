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

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.ContextMatcher;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.ResetVisitor;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Pool for transformation trees.
 * @author Simon Templer
 */
public class TransformationTreePool {

	private final Alignment alignment;
	
	private final ListMultimap<TypeDefinition, TransformationTree> trees;
	
	private final ResetVisitor resetVisitor = new ResetVisitor();

	private final ContextMatcher matcher;

	/**
	 * Create a transformation tree pool.
	 * @param alignment the associated alignment
	 * @param matcher the context matcher to apply to a created tree
	 */
	public TransformationTreePool(Alignment alignment, ContextMatcher matcher) {
		this.alignment = alignment;
		this.matcher = matcher;
		
		trees = ArrayListMultimap.create();
	}

	/**
	 * Get a transformation tree from the pool.
	 * @param targetType the target type for the transformation tree
	 * @return the transformation tree
	 */
	public TransformationTree getTree(TypeDefinition targetType) {
		synchronized (trees) {
			List<TransformationTree> treeList = trees.get(targetType);
			if (treeList.isEmpty()) {
				TransformationTree tree = new TransformationTreeImpl(
						targetType, alignment);
				if (matcher != null) {
					matcher.findMatches(tree);
				}
				return tree;
			}
			else {
				TransformationTree tree = treeList.remove(0);
				return tree;
			}
		}
	}
	
	/**
	 * Release a tree to the pool. 
	 * @param tree the transformation tree that is no longer needed
	 */
	public void releaseTree(TransformationTree tree) {
		tree.accept(resetVisitor); // remove all annotations
		synchronized (trees) {
			trees.put(tree.getType(), tree);
		}
	}

}
