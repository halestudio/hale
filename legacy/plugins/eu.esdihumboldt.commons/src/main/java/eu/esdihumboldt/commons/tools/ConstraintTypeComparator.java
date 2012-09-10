/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;

/**
 * This comparator can be used to find out whether two Constraint
 * implementations share a common interface (except Constraint and Serializable,
 * that is.)
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: ConstraintTypeComparator.java,v 1.1 2007-11-06 10:26:29 pitaeva
 *          Exp $
 */
public class ConstraintTypeComparator implements Comparator {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object _constraint1, Object _constraint2) {
		if (_constraint1 == null || _constraint2 == null) {
			throw new NullPointerException();
		}

		// get the Classes for each of the Constraints to compare.
		List<Class> c1_supertypes = this.getSupertypes(_constraint1.getClass());
		List<Class> c2_supertypes = this.getSupertypes(_constraint2.getClass());

		// use the difference of the constraint.class hash values as natural
		// ordering.
		int result = _constraint1.getClass().hashCode()
				- _constraint2.getClass().hashCode();

		// ..but also make cases equal where a common subinterface of constraint
		// is implemented.
		for (Class this_class : c1_supertypes) {
			if (c2_supertypes.contains(this_class)) {
				if (!this_class.equals(Serializable.class)) {
					result = 0;
				}
			}
		}
		return result;
	}

	/**
	 * Recursive method for determining the inheritance hierarchy of the passed
	 * class up unto Constraint.
	 * 
	 * @param _c
	 *            the Class for which to determine the inheritance hierarchy.
	 * @return a List of Class objects, starting with the one passed in and
	 *         ending with {@link Constraint}.
	 */
	private List<Class> getSupertypes(Class _c) {
		List<Class> result = new ArrayList<Class>();
		// Constraint.class is the stopper value.
		if (!_c.equals(Constraint.class)) {
			result.add(_c);
			for (Class this_class : _c.getInterfaces()) {
				result.addAll(this.getSupertypes(this_class));
			}
		}
		return result;
	}

}
