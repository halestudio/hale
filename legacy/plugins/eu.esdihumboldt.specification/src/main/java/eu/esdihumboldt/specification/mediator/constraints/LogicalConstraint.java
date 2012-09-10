/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator.constraints;

import java.util.List;

/**
 * This is an interface for logical constraints, such as AND, OR, XOR and
 * EQUALS. It can be used to test 2..* constraints according to the logical
 * operator that was specified.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface LogicalConstraint extends Constraint {

	/**
	 * @return a List containing all {@link Constraint}s bound together by a
	 *         {@link LogicalConstraint}.
	 */
	public List<Constraint> getBoundConstraints();

	/**
	 * @return the type of the logical operator binding the Constraints.
	 */
	public LogicalOperator getLogicalOperator();

	/**
	 * This enumeration gives the types of logical operators that can be used.
	 * In addition to those types available within the OGC Filter Encoding
	 * specification, an exclusive or (XOR) and a equals operator (EQUALS) is
	 * added.
	 */
	public enum LogicalOperator {
		/** will return true if all bound Constraints are true. */
		AND,
		/** will return true if at least one bound Constraint is true. */
		OR,
		/** negates an expression. */
		NOT,
		/** will return true if exactly one bound Constraint is true. */
		XOR,
		/** will return true if all bound Constraints return the same value. */
		EQUALS
	}

}
