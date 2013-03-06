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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor;

import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.Leftovers;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.TargetContext;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.util.Pair;

/**
 * DuplicationVisitor visits source nodes to duplicate the transformation tree
 * for their leftovers.
 * 
 * @author Simon Templer
 */
public class DuplicationVisitor extends AbstractSourceToTargetVisitor {

	private final TransformationTree tree;
	private final TransformationLog log;

	/**
	 * Creates a duplication visitor.
	 * 
	 * @param tree the transformation tree
	 * @param log the transformation log
	 */
	public DuplicationVisitor(TransformationTree tree, TransformationLog log) {
		this.tree = tree;
		this.log = log;
	}

	/**
	 * @see AbstractTransformationNodeVisitor#visit(CellNode)
	 */
	@Override
	public boolean visit(CellNode cell) {
		// don't descend further
		return false;
	}

	/**
	 * @see AbstractTransformationNodeVisitor#visit(SourceNode)
	 */
	@Override
	public boolean visit(SourceNode source) {
		Leftovers leftovers = source.getLeftovers();

		if (leftovers != null) {
			// identify context match (if possible)
			TransformationContext context = source.getContext();

			if (context == null) {
				// no transformation context match defined
				log.warn(log.createMessage(
						"Multiple values for source node w/o transformation context match", null));
			}
			else {
				Pair<SourceNode, Set<Cell>> leftover;
				// completely consume leftovers
				while ((leftover = leftovers.consumeValue()) != null) {
					context.duplicateContext(source, leftover.getFirst(), leftover.getSecond(), log);
					// XXX is this the place where this should be propagated to
					// the duplicated source children?
					// XXX trying it out
					SourceNode node = leftover.getFirst();
					Object value = node.getValue();
					if (value instanceof Group) {
						InstanceVisitor instanceVisitor = new InstanceVisitor(null, null, log);
						for (SourceNode child : node.getChildren(instanceVisitor
								.includeAnnotatedNodes())) {
							// annotate children with leftovers
							child.accept(instanceVisitor);
							// run the duplication on the children
							child.accept(this);
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * @see TransformationNodeVisitor#includeAnnotatedNodes()
	 */
	@Override
	public boolean includeAnnotatedNodes() {
		// follow annotated nodes in case of an instance family that is
		// important
		return true;
	}

	/**
	 * Duplicates assignments without connections to source nodes. Should be
	 * called after all duplication is done.
	 */
	public void doAugmentationTrackback() {
		TargetContext.augmentationTrackback(tree);
	}
}
