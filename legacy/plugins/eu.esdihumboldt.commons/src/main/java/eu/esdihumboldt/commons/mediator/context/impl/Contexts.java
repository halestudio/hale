/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: Contexts.java,v 1.1 2007-10-19 10:03:13 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.context.impl;

import eu.esdihumboldt.specification.mediator.context.Context;

/**
 * Class Contexts.
 * 
 * @version $Revision: 1.1 $ $Date: 2007-10-19 10:03:13 $
 */
public abstract class Contexts implements java.io.Serializable, Context {

	// ----------------/
	// - Constructors -/
	// ----------------/

	public Contexts() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method isValid.
	 * 
	 * @return true if this object is valid according to the schema
	 */
	public boolean isValid() {
		try {
			validate();
		} catch (org.exolab.castor.xml.ValidationException vex) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 
	 * @throws org.exolab.castor.xml.ValidationException
	 *             if this object is an invalid instance according to the schema
	 */
	public void validate() throws org.exolab.castor.xml.ValidationException {
		org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
		validator.validate(this);
	}

}
