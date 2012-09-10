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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor;

import java.util.Set;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.Leftovers;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl.TargetContext;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.util.Pair;

/**
 * DuplicationVisitor visits source nodes to duplicate the transformation tree
 * for their leftovers.
 * 
 * @author Simon Templer
 */
public class DuplicationVisitor extends AbstractSourceToTargetVisitor {

	private static final ALogger log = ALoggerFactory.getLogger(DuplicationVisitor.class);
	private final TransformationTree tree;

	/**
	 * Creates a duplication visitor.
	 * 
	 * @param tree the transformation tree
	 */
	public DuplicationVisitor(TransformationTree tree) {
		this.tree = tree;
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
				// XXX warn instead? XXX transformation log instead?
				log.error("Multiple values for source node w/o transformation context match");
			}
			else {
				Pair<SourceNode, Set<Cell>> leftover;
				// completely consume leftovers
				while ((leftover = leftovers.consumeValue()) != null) {
					context.duplicateContext(source, leftover.getFirst(), leftover.getSecond());
					// XXX is this the place where this should be propagated to
					// the duplicated source children?
					// XXX trying it out
					SourceNode node = leftover.getFirst();
					Object value = node.getValue();
					if (value instanceof Group) {
						InstanceVisitor instanceVisitor = new InstanceVisitor(null, null);
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
