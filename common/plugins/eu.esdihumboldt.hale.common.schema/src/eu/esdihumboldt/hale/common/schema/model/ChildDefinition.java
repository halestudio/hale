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

package eu.esdihumboldt.hale.common.schema.model;

/**
 * Definition of a property or property group
 * 
 * @param <C> the supported constraint type
 * 
 * @author Simon Templer
 */
public interface ChildDefinition<C> extends Definition<C> {

	/**
	 * Get the parent type of the child. This can either be the declaring type
	 * or a sub-type.
	 * 
	 * @return the parent type of the property
	 */
	public TypeDefinition getParentType();

	/**
	 * Get the definition of the group declaring the property.
	 * 
	 * @return the group declaring the property
	 */
	public DefinitionGroup getDeclaringGroup();

	/**
	 * Return as a property definition is possible. This is convenience for
	 * avoiding casts and instanceof expressions when handling children.
	 * 
	 * @return a property definition if this child definition is one, otherwise
	 *         <code>null</code>
	 */
	public PropertyDefinition asProperty();

	/**
	 * Return as a group definition is possible. This is convenience for
	 * avoiding casts and instanceof expressions when handling children.
	 * 
	 * @return a group definition if this child definition is one, otherwise
	 *         <code>null</code>
	 */
	public GroupPropertyDefinition asGroup();

}