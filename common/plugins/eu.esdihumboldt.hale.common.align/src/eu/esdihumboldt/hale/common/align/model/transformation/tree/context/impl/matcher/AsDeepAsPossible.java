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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.matcher;

import java.util.Collections;
import java.util.Stack;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.ContextMatcher;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.TargetContext;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.AbstractTargetToSourceVisitor;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;

/**
 * Simple context matching strategy that looks for connected 
 * @author Simon Templer
 */
public class AsDeepAsPossible implements ContextMatcher {

	/**
	 * Builds a stack of target node candidates that while traversing the
	 * transformation tree, assigning the last candidate to source nodes
	 * as a transformation context.
	 */
	private static class ContextVisitor extends AbstractTargetToSourceVisitor {
		
		private final Stack<TargetNode> candidates = new Stack<TargetNode>();

		@Override
		public boolean visit(TargetNode target) {
			if (isCandidate(target)) {
				candidates.push(target);
			}
			return true;
		}

		@Override
		public void leave(TargetNode target) {
			if (isCandidate(target)) {
				candidates.pop();
			}
		}

		@Override
		public boolean visit(SourceNode source) {
			if (!candidates.isEmpty()) {
				source.setContext(new TargetContext(
						Collections.singleton(candidates.peek())));
			}
			return true;
		}

		/**
		 * Determines if a target node is a candidate for a context match.
		 * For this the cardinality of the corresponding definition is checked.
		 * @param target the target node to check
		 * @return if the target node is a candidate for a context match
		 */
		private boolean isCandidate(TargetNode target) {
			ChildDefinition<?> def = target.getDefinition();
			Cardinality cardinality = DefinitionUtil.getCardinality(def);
			if (cardinality != null) {
				return cardinality.getMaxOccurs() == Cardinality.UNBOUNDED
						|| cardinality.getMaxOccurs() > 1;
			}
			return false;
		}

		/**
		 * @see TransformationNodeVisitor#includeAnnotatedNodes()
		 */
		@Override
		public boolean includeAnnotatedNodes() {
			// if there are any, just deal with them also
			// though in reality, annotated nodes only should be there AFTER context matching
			return true;
		}

	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.context.ContextMatcher#findMatches(eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree)
	 */
	@Override
	public void findMatches(TransformationTree tree) {
		tree.accept(new ContextVisitor());
	}

}
