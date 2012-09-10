/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: CommunicationDetailsType.java,v 1.1 2007-10-19 10:03:07 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

/**
 * It contains the information regarding the communication details for the
 * person.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2007-10-19 10:03:07 $
 */
public abstract class CommunicationDetailsType implements java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * Field _communicationDetailDescription.
	 */
	private java.lang.String _communicationDetailDescription;

	/**
	 * Field _personCommunication.
	 */
	private eu.esdihumboldt.commons.mediator.usermanagement.impl.PersonCommunicationImpl _personCommunication;

	/**
	 * Field _communicationValue.
	 */
	private java.lang.String _communicationValue;

	/**
	 * Field _defaultCommunication.
	 */
	private boolean _defaultCommunication;

	/**
	 * keeps track of state for field: _defaultCommunication
	 */
	private boolean _has_defaultCommunication;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public CommunicationDetailsType() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
     */
	public void deleteDefaultCommunication() {
		this._has_defaultCommunication = false;
	}

	/**
	 * Returns the value of field 'communicationDetailDescription'.
	 * 
	 * @return the value of field 'CommunicationDetailDescription'.
	 */
	public java.lang.String getCommunicationDetailDescription() {
		return this._communicationDetailDescription;
	}

	/**
	 * Returns the value of field 'communicationValue'.
	 * 
	 * @return the value of field 'CommunicationValue'.
	 */
	public java.lang.String getCommunicationValue() {
		return this._communicationValue;
	}

	/**
	 * Returns the value of field 'defaultCommunication'.
	 * 
	 * @return the value of field 'DefaultCommunication'.
	 */
	public boolean getDefaultCommunication() {
		return this._defaultCommunication;
	}

	/**
	 * Returns the value of field 'personCommunication'.
	 * 
	 * @return the value of field 'PersonCommunication'.
	 */
	public eu.esdihumboldt.commons.mediator.usermanagement.impl.PersonCommunicationImpl getPersonCommunication() {
		return this._personCommunication;
	}

	/**
	 * Method hasDefaultCommunication.
	 * 
	 * @return true if at least one DefaultCommunication has been added
	 */
	public boolean hasDefaultCommunication() {
		return this._has_defaultCommunication;
	}

	/**
	 * Returns the value of field 'defaultCommunication'.
	 * 
	 * @return the value of field 'DefaultCommunication'.
	 */
	public boolean isDefaultCommunication() {
		return this._defaultCommunication;
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
	 * Sets the value of field 'communicationDetailDescription'.
	 * 
	 * @param communicationDetailDescription
	 *            the value of field 'communicationDetailDescription'.
	 */
	public void setCommunicationDetailDescription(
			final java.lang.String communicationDetailDescription) {
		this._communicationDetailDescription = communicationDetailDescription;
	}

	/**
	 * Sets the value of field 'communicationValue'.
	 * 
	 * @param communicationValue
	 *            the value of field 'communicationValue'.
	 */
	public void setCommunicationValue(final java.lang.String communicationValue) {
		this._communicationValue = communicationValue;
	}

	/**
	 * Sets the value of field 'defaultCommunication'.
	 * 
	 * @param defaultCommunication
	 *            the value of field 'defaultCommunication'.
	 */
	public void setDefaultCommunication(final boolean defaultCommunication) {
		this._defaultCommunication = defaultCommunication;
		this._has_defaultCommunication = true;
	}

	/**
	 * Sets the value of field 'personCommunication'.
	 * 
	 * @param personCommunication
	 *            the value of field 'personCommunication'.
	 */
	public void setPersonCommunication(
			final eu.esdihumboldt.commons.mediator.usermanagement.impl.PersonCommunicationImpl personCommunication) {
		this._personCommunication = personCommunication;
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
