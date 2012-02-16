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
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.util.Pair;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class DuplicationVisitor extends AbstractSourceToTargetVisitor {
	
	private static final ALogger log = ALoggerFactory.getLogger(DuplicationVisitor.class);

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
				//XXX warn instead? XXX transformation log instead?
//				log.error("Multiple values for source node w/o transformation context match");
			}
			else {
				Pair<SourceNode, Set<Cell>> leftover;
				// completely consume leftovers
				while ((leftover = leftovers.consumeValue()) != null) {
					context.duplicateContext(source, leftover.getFirst(), 
							leftover.getSecond());
					//XXX is this the place where this should be propagated to the duplicated source children?
					//XXX trying it out
					SourceNode node = leftover.getFirst();
					Object value = node.getValue();
					if (value instanceof Group) {
						// annotate children with leftovers
						InstanceVisitor instanceVisitor = new InstanceVisitor((Group) value);
						node.accept(instanceVisitor);
						
						// run the duplication on the children
						node.accept(this);
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
		//FIXME is this still true?!
		// annotated nodes are ignored, as these are handled when created
		return false;
	}

}
