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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Target group or property
 * 
 * @author Simon Templer
 */
public interface TargetNode extends GroupNode {

	/**
	 * Name of the value defined annotation. It specifies if a value is defined
	 * for the source node.
	 */
	public static final String ANNOTATION_RESULT_DEFINED = "result:defined";

	/**
	 * Name of the value annotation. It specifies a concrete value for the node
	 * from an instance.
	 */
	public static final String ANNOTATION_RESULT = "result";

	/**
	 * Get the assignment to this property
	 * 
	 * @return the property assignments
	 */
	public Set<CellNode> getAssignments();

	/**
	 * Get the associated definition
	 * 
	 * @return the group or property definition
	 */
	public ChildDefinition<?> getDefinition();

	/**
	 * Get the full entity definition associated with the node.
	 * 
	 * @return the entity definition
	 */
	public EntityDefinition getEntityDefinition();

	/**
	 * Get the assignment names for the given cell. These are the names of the
	 * cell entities the node is associated to.
	 * 
	 * @param assignment the assigned cell
	 * @return the assignment names
	 */
	public Set<String> getAssignmentNames(CellNode assignment);

	/**
	 * Get if the node result is defined.
	 * 
	 * @see #ANNOTATION_RESULT_DEFINED
	 * @return the value of the result defined annotation, or <code>false</code>
	 *         if it is not set
	 */
	public boolean isDefined();

	/**
	 * Set the value of the result defined annotation.
	 * 
	 * @see #ANNOTATION_RESULT_DEFINED
	 * @param defined if the node result is defined
	 */
	public void setDefined(boolean defined);

	/**
	 * Get the result value of the node as assigned through a property
	 * transformation.
	 * 
	 * @see #ANNOTATION_RESULT
	 * @return the property value associated to the node, may be
	 *         <code>null</code>
	 */
	public Object getResult();

	/**
	 * Set the value of the result annotation. When setting a result the value
	 * of the result defined annotation is set to <code>true</code>.
	 * 
	 * @see #ANNOTATION_RESULT
	 * @see #ANNOTATION_RESULT_DEFINED
	 * @param value the result value of the node
	 */
	public void setResult(Object value);

}
