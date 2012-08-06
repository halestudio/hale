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

package eu.esdihumboldt.hale.common.schema.model.constraint;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.ParentBound;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint that represents the population of a definition, e.g. how many
 * instances of a type are there and how many overall occurrences of a property
 * are there. 
 * @author Simon Templer
 */
@Constraint(mutable = true)
@ParentBound
public class Population implements PropertyConstraint, TypeConstraint,
		GroupPropertyConstraint {
	
	private int count = 0;

	/**
	 * Create a population constraint initialized with a zero population. 
	 */
	public Population() {
		super();
	}

	/**
	 * Get the population count.
	 * @return the population count
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Reset the population count to zero.
	 */
	public void reset() {
		count = 0;
	}
	
	/**
	 * Increase the population count by one.
	 */
	public void increase() {
		count++;
	}
	
	/**
	 * Increase the population count.
	 * @param number by how much to increase the count
	 */
	public void increase(int number) {
		count += number;
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		return false;
	}

}
