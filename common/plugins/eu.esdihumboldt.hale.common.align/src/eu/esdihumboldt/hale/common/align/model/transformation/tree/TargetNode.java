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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Target group or property
 * @author Simon Templer
 */
public interface TargetNode extends GroupNode {
	
	/**
	 * Get the assignment to this property
	 * @return the property assignments
	 */
	public Set<CellNode> getAssignments();
	
	/**
	 * Get the associated definition
	 * @return the group or property definition
	 */
	public ChildDefinition<?> getDefinition();
	
	/**
	 * Get the full entity definition associated with the node.
	 * @return the entity definition
	 */
	public EntityDefinition getEntityDefinition();

	/**
	 * Get the assignment names for the given cell. These are the names of the
	 * cell entities the node is associated to.
	 * @param assignment the assigned cell
	 * @return the assignment name or <code>null</code>
	 */
	public Set<String> getAssignmentNames(CellNode assignment);

}
