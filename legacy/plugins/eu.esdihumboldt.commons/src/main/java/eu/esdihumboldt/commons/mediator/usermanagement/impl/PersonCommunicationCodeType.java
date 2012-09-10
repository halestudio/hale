/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: PersonCommunicationCodeType.java,v 1.1 2007-10-19 10:03:06 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

/**
 * This element is container to hold the coded list of the communication method
 * of the person.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2007-10-19 10:03:06 $
 */
public abstract class PersonCommunicationCodeType implements
		java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * Field _personCommunicationTypeCoded.
	 */
	private eu.esdihumboldt.commons.mediator.usermanagement.types.impl.PersonCommunicationTypeCodedType _personCommunicationTypeCoded;

	/**
	 * Field _personCommunicationTypeCodedOther.
	 */
	private java.lang.String _personCommunicationTypeCodedOther;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public PersonCommunicationCodeType() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Returns the value of field 'personCommunicationTypeCoded'.
	 * 
	 * @return the value of field 'PersonCommunicationTypeCoded'.
	 */
	public eu.esdihumboldt.commons.mediator.usermanagement.types.impl.PersonCommunicationTypeCodedType getPersonCommunicationTypeCoded() {
		return this._personCommunicationTypeCoded;
	}

	/**
	 * Returns the value of field 'personCommunicationTypeCodedOther'.
	 * 
	 * @return the value of field 'PersonCommunicationTypeCodedOther'.
	 */
	public java.lang.String getPersonCommunicationTypeCodedOther() {
		return this._personCommunicationTypeCodedOther;
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
	 * Sets the value of field 'personCommunicationTypeCoded'.
	 * 
	 * @param personCommunicationTypeCoded
	 *            the value of field 'personCommunicationTypeCoded'.
	 */
	public void setPersonCommunicationTypeCoded(
			final eu.esdihumboldt.commons.mediator.usermanagement.types.impl.PersonCommunicationTypeCodedType personCommunicationTypeCoded) {
		this._personCommunicationTypeCoded = personCommunicationTypeCoded;
	}

	/**
	 * Sets the value of field 'personCommunicationTypeCodedOther'.
	 * 
	 * @param personCommunicationTypeCodedOther
	 *            the value of field 'personCommunicationTypeCodedOther'.
	 */
	public void setPersonCommunicationTypeCodedOther(
			final java.lang.String personCommunicationTypeCodedOther) {
		this._personCommunicationTypeCodedOther = personCommunicationTypeCodedOther;
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
