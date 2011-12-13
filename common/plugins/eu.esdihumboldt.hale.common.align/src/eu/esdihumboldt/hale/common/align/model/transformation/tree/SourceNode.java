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

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Represents a source type, group or property
 * @author Simon Templer
 */
public interface SourceNode extends TransformationNode {
	
	/**
	 * Name of the occurrence annotation 
	 */
	public static final String ANNOTATION_OCCURRENCE = "occurrence";
	
	/**
	 * Name of the value annotation 
	 */
	public static final String ANNOTATION_VALUE = "value";
	
	/**
	 * Get the associated definition
	 * @return the type, group or property definition
	 */
	public Definition<?> getDefinition();
	
	/**
	 * Get the group or property's parent
	 * @return the parent node or <code>null</code>
	 */
	public SourceNode getParent();
	
	/**
	 * Get the full entity definition associated with the node.
	 * @return the entity definition
	 */
	public EntityDefinition getEntityDefinition();

	/**
	 * Add a child node.
	 * @param child the child source node
	 */
	public void addChild(SourceNode child);
	
	/**
	 * Get the node's children.
	 * @return the collection of child nodes
	 */
	public Collection<SourceNode> getChildren();
	
	/**
	 * Get the occurrence of the node in the context of specific source
	 * instances.
	 * @see #ANNOTATION_OCCURRENCE
	 * @return the value of the occurrence annotation, or <code>0</code> if it
	 *   is not set
	 */
	public int getOccurrence();
	
	/**
	 * Set the value of the occurrence annotation.
	 * @see #ANNOTATION_OCCURRENCE
	 * @param occurrence the occurence of the node in the context of specific
	 *   source instances
	 */
	public void setOccurrence(int occurrence);
	
	/**
	 * Get the value of the node in the context of a specific source
	 * instance.
	 * @see #ANNOTATION_VALUE
	 * @return the property value associated to the node, may be 
	 *   <code>null</code>
	 */
	public Object getValue();
	
	/**
	 * Set the value of the value annotation.
	 * @see #ANNOTATION_VALUE
	 * @param value the value of the node in the context of a specific
	 *   source instance
	 */
	public void setValue(Object value);

//	/**
//	 * Add a relation.
//	 * @param cellNode the cell node representing the relation
//	 */
//	public void addRelation(CellNode cellNode);
//	
//	/**
//	 * Get the relations associated to the source node.
//	 * @return the associated relations
//	 */
//	public Collection<CellNode> getRelations();

}
