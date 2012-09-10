/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.tools;

import eu.esdihumboldt.specification.mediator.TypeKey;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.util.ConstraintTypeComparator;

/**
 * This serves as a wrapper/proxy to Constraints for determining whether two
 * {@link Constraint} implementations implement the same subinterface branch.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: ConstraintTypeKey.java,v 1.1 2007-11-06 10:26:29 pitaeva Exp $
 */
public class ConstraintTypeKey implements TypeKey {

	private static final long serialVersionUID = -6702163039383778188L;

	// Fields ..................................................................
	private Constraint constraint;

	private static final ConstraintTypeComparator ctc = new ConstraintTypeComparator();

	// Constructors ............................................................
	public ConstraintTypeKey(Constraint _c) {
		this.constraint = _c;
	}

	// Operations ..............................................................
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConstraintTypeKey) {
			ConstraintTypeKey ctk = (ConstraintTypeKey) obj;
			return ConstraintTypeKey.ctc.compare(ctk.constraint,
					this.constraint) == 0;
		} else {
			throw new ClassCastException("The object passed in was not a "
					+ "ConstraintTypeKey: " + obj.getClass());
		}
	}

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object obj) {
		if (obj instanceof ConstraintTypeKey) {
			ConstraintTypeKey ctk = (ConstraintTypeKey) obj;
			return ConstraintTypeKey.ctc.compare(ctk.constraint,
					this.constraint);
		} else {
			throw new ClassCastException("The object passed in was not a "
					+ "ConstraintTypeKey: " + obj.getClass());
		}
	}

	/**
	 * This implementation ensures that TypeKeys containing the same type of
	 * {@link Constraint} will have the same HashValue.
	 */
	@Override
	public int hashCode() {
		return this.getConstraintType(this.constraint.getClass())
				.getCanonicalName().hashCode();
	}

	/**
	 * Recursive method for determining the inheritance hierarchy of the passed
	 * class up unto Constraint.
	 * 
	 * @param _c
	 *            the Class for which to determine the constraint type.
	 * @return a Class object identifying the correct interface.
	 */
	@SuppressWarnings("unchecked")
	private Class getConstraintType(Class _c) {
		Class result = Constraint.class;
		for (Class this_class : _c.getInterfaces()) {
			for (Class this_super_class : this_class.getInterfaces()) {
				if (this_super_class.equals(Constraint.class)) {
					result = this_class;
				}
			}
		}
		return result;
	}

}
