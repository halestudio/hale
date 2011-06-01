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

package eu.esdihumboldt.hale.schema.model;

/**
 * Marks definition constraints.<br>
 * <br>
 * Concrete implementations must have a default constructor or a constructor
 * that takes a {@link Definition} as an argument that will create
 * the constraint with its default settings as this will be used when a 
 * constraint of that type is requested for a definition where it does not
 * exist. If both a default and a {@link Definition} constructor are available
 * the constructor that takes a definition is preferred if possible.
 * @see TypeDefinition#getConstraint(Class)
 * @see PropertyDefinition#getConstraint(Class)
 * 
 * @author Simon Templer
 */
public interface Constraint {

	/**
	 * States if the constraint is mutable and can be manipulated.<br>
	 * <br>
	 * NOTE: A mutable constraint is not ensured to be thread safe
	 * 
	 * @return if the constraint is mutable
	 */
	public boolean isMutable();
	
}
