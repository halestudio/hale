/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: OrganizationAddressType.java,v 1.5 2007-11-16 09:17:50 jamess Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import java.util.TimeZone;

import eu.esdihumboldt.specification.mediator.usermanagement.OrganizationAddress;

/**
 * It describes the address-specific address information.
 * 
 * 
 * @version $Revision: 1.5 $ $Date: 2007-11-16 09:17:50 $
 */
public abstract class OrganizationAddressType extends
		eu.esdihumboldt.commons.mediator.usermanagement.impl.AddressType
		implements java.io.Serializable, OrganizationAddress {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	private long id;

	/**
	 * Field _timeZone.
	 */
	private TimeZone timeZone;

	/** priority of the address in the address list of the organization */

	private int priority;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public OrganizationAddressType() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Returns the value of field 'timeZone'.
	 * 
	 * @return the value of field 'TimeZone'.
	 */
	public TimeZone getTimeZone() {
		return this.timeZone;
	}

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
	 * Sets the value of field 'timeZone'.
	 * 
	 * @param timeZone
	 *            the value of field 'timeZone'.
	 */
	public void setTimeZone(final TimeZone timezone) {
		this.timeZone = timezone;

	}

	@Deprecated
	public CountryCode getCountry() {
		// TODO Auto-generated method stub
		return null;
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
