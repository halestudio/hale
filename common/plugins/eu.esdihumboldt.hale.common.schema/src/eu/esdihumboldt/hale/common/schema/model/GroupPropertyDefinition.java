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
 * Defines a group property
 * 
 * @author Simon Templer
 */
public interface GroupPropertyDefinition extends DefinitionGroup,
		ChildDefinition<GroupPropertyConstraint> {

	// concrete typed interface

	/**
	 * States if the group may be flattened, i.e. that the group's children may
	 * be added to the group's parent instead of itself.<br>
	 * This can be reasonable for groups that are only created because at
	 * creation time the children are not yet determined.
	 * 
	 * @return if the group may be replaced by its children
	 */
	public boolean allowFlatten();

}
