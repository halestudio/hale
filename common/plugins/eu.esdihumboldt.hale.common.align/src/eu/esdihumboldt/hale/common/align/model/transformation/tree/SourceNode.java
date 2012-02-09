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
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Represents a source type, group or property
 * @author Simon Templer
 */
public interface SourceNode extends TransformationNode {
	
	/**
	 * Name of the value defined annotation. It specifies if a value is defined 
	 * for the source node.
	 */
	public static final String ANNOTATION_VALUE_DEFINED = "value:defined";
	
	/**
	 * Name of the value annotation. It specifies a concrete value for the node
	 * from an instance.
	 */
	public static final String ANNOTATION_VALUE = "value";
	
	/**
	 * Name of the children annotation. It represents a list of additional
	 * children.
	 */
	public static final String ANNOTATION_CHILDREN = "children";
	
	/**
	 * Name of the leftovers annotation. It represents values that are not
	 * (yet) represented in their own source node. 
	 */
	public static final String ANNOTATION_LEFTOVERS = "leftovers";
	
	/**
	 * Name of the relations annotation. It represents a list of additional
	 * relations.
	 */
	public static final String ANNOTATION_RELATIONS = "relations";
	
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
	 * Add a child node as annotation. This means resetting the node will remove
	 * the child.
	 * @param child the child node to add
	 */
	public void addAnnotatedChild(SourceNode child);
	
	/**
	 * Get the node's children.
	 * @param includeAnnotated if annotated children should be included
	 * @return the collection of child nodes
	 */
	public Collection<SourceNode> getChildren(boolean includeAnnotated);
	
	/**
	 * Set the transformation context.
	 * @param context the transformation context
	 */
	public void setContext(TransformationContext context);
	
	/**
	 * Get the associated transformation context.
	 * @return the transformation context, may be <code>null</code>
	 */
	public TransformationContext getContext();
	
	/**
	 * Set the left over values associated to the node's entity, but not
	 * represented by the node.
	 * @param leftovers the leftovers
	 */
	public void setLeftovers(Leftovers leftovers);
	
	/**
	 * Get the left over values associated to the node's entity, but not
	 * represented by the node.
	 * @return the leftovers, may be <code>null</code>
	 */
	public Leftovers getLeftovers();
	
	/**
	 * Get if the source node value is defined.
	 * @see #ANNOTATION_VALUE_DEFINED
	 * @return the value of the defined annotation, or <code>false</code> if it
	 *   is not set
	 */
	public boolean isDefined();
	
	/**
	 * Set the value of the defined annotation.
	 * @see #ANNOTATION_VALUE_DEFINED
	 * @param defined if the node value is defined
	 */
	public void setDefined(boolean defined);
	
	/**
	 * Get the value of the node in the context of a specific source
	 * instance.
	 * @see #ANNOTATION_VALUE
	 * @return the property value associated to the node, may be 
	 *   <code>null</code>
	 */
	public Object getValue();
	
	/**
	 * Set the value of the value annotation. When setting a value the value
	 * of the defined annotation is set to <code>true</code>.
	 * @see #ANNOTATION_VALUE
	 * @see #ANNOTATION_VALUE_DEFINED
	 * @param value the value of the node in the context of a specific
	 *   source instance
	 */
	public void setValue(Object value);

	/**
	 * Add a relation.
	 * @param cellNode the cell node representing the relation
	 */
	public void addRelation(CellNode cellNode);
	
	/**
	 * Add a cell node as annotated relation. This means resetting the node 
	 * will remove the relation.
	 * @param relation the cell node representing the relation
	 */
	public void addAnnotatedRelation(CellNode relation);
	
	/**
	 * Get the relations associated to the source node.
	 * @param includeAnnotated if annotated relations should be included
	 * @return the associated relations
	 */
	public Collection<CellNode> getRelations(boolean includeAnnotated);

}
