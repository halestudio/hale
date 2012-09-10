/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: PersonProfileType.java,v 1.3 2007-10-24 13:42:54 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.security.cert.X509Certificate;

import eu.esdihumboldt.specification.mediator.usermanagement.PersonProfile;

/**
 * ThisType contains the profile of the HUMBOLDT User.
 * 
 * 
 * @version $Revision: 1.3 $ $Date: 2007-10-24 13:42:54 $
 */
public abstract class PersonProfileType implements java.io.Serializable,
		PersonProfile {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	private long id;
	/**
	 * Field _title.
	 */
	private eu.esdihumboldt.commons.mediator.usermanagement.types.impl.TitleType title;

	/**
	 * Field _academicTitle.
	 */
	private eu.esdihumboldt.commons.mediator.usermanagement.types.impl.AcademicTitleType academicTitle;

	/**
	 * Field _firstName.
	 */
	private java.lang.String firstName;

	/**
	 * Field _lastName.
	 */
	private java.lang.String lastName;

	/**
	 * Field _middleName.
	 */
	private java.lang.String middleName;

	/**
	 * Field _language.
	 */
	private String language;

	/**
	 * Field _numberFormat.
	 */
	private NumberFormat numberFormat;

	/**
	 * Field _dateFormat.
	 */
	private DateFormat dateFormat;

	/**
	 * Field _timeFormat.
	 */
	private SimpleDateFormat timeFormat;

	/**
	 * Field _timeZone.
	 */
	private TimeZone timeZone;

	/**
	 * Field _personalAddress.
	 */
	private PersonalAddress personalAddress;

	/**
	 * This element contains any free-form text pertinent to the PersonProfile.
	 * This element may contain notes or any other similar information that is
	 * not contained explicitly in another structure.
	 * 
	 */
	private java.lang.String generalNotes;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public PersonProfileType() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Returns the value of field 'academicTitle'.
	 * 
	 * @return the value of field 'AcademicTitle'.
	 */
	public eu.esdihumboldt.commons.mediator.usermanagement.types.impl.AcademicTitleType getAcademicTitle() {
		return this.academicTitle;
	}

	/**
	 * Returns the value of field 'dateFormat'.
	 * 
	 * @return the value of field 'DateFormat'.
	 */
	public DateFormat getDateFormat() {
		return this.dateFormat;
	}

	/**
	 * Returns the value of field 'firstName'.
	 * 
	 * @return the value of field 'FirstName'.
	 */
	public java.lang.String getFirstName() {
		return this.firstName;
	}

	/**
	 * Returns the value of field 'generalNotes'. The field 'generalNotes' has
	 * the following description: This element contains any free-form text
	 * pertinent to the PersonProfile. This element may contain notes or any
	 * other similar information that is not contained explicitly in another
	 * structure.
	 * 
	 * 
	 * @return the value of field 'GeneralNotes'.
	 */
	public java.lang.String getGeneralNotes() {
		return this.generalNotes;
	}

	/**
	 * Returns the value of field 'language'.
	 * 
	 * @return the value of field 'Language'.
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Returns the value of field 'lastName'.
	 * 
	 * @return the value of field 'LastName'.
	 */
	public java.lang.String getLastName() {
		return this.lastName;
	}

	/**
	 * Returns the value of field 'middleName'.
	 * 
	 * @return the value of field 'MiddleName'.
	 */
	public java.lang.String getMiddleName() {
		return this.middleName;
	}

	/**
	 * Returns the value of field 'numberFormat'.
	 * 
	 * @return the value of field 'NumberFormat'.
	 */
	public NumberFormat getNumberFormat() {
		return this.numberFormat;
	}

	/**
	 * Returns the value of field 'personalAddress'.
	 * 
	 * @return the value of field 'PersonalAddress'.
	 */
	public PersonalAddress getPersonalAddress() {
		return this.personalAddress;
	}

	/**
	 * Returns the value of field 'timeFormat'.
	 * 
	 * @return the value of field 'TimeFormat'.
	 */
	public SimpleDateFormat getTimeFormat() {
		return this.timeFormat;
	}

	/**
	 * Returns the value of field 'timeZone'.
	 * 
	 * @return the value of field 'TimeZone'.
	 */
	public TimeZone getTimeZone() {
		return this.timeZone;
	}

	/**
	 * Returns the value of field 'title'.
	 * 
	 * @return the value of field 'Title'.
	 */
	public eu.esdihumboldt.commons.mediator.usermanagement.types.impl.TitleType getTitle() {
		return this.title;
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
	 * Sets the value of field 'academicTitle'.
	 * 
	 * @param academicTitle
	 *            the value of field 'academicTitle'.
	 */
	public void setAcademicTitle(
			final eu.esdihumboldt.commons.mediator.usermanagement.types.impl.AcademicTitleType academicTitle) {
		this.academicTitle = academicTitle;
	}

	/**
	 * Sets the value of field 'dateFormat'.
	 * 
	 * @param dateFormat
	 *            the value of field 'dateFormat'.
	 */
	public void setDateFormat(final DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * Sets the value of field 'firstName'.
	 * 
	 * @param firstName
	 *            the value of field 'firstName'.
	 */
	public void setFirstName(final java.lang.String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the value of field 'generalNotes'. The field 'generalNotes' has the
	 * following description: This element contains any free-form text pertinent
	 * to the PersonProfile. This element may contain notes or any other similar
	 * information that is not contained explicitly in another structure.
	 * 
	 * 
	 * @param generalNotes
	 *            the value of field 'generalNotes'.
	 */
	public void setGeneralNotes(final java.lang.String generalNotes) {
		this.generalNotes = generalNotes;
	}

	/**
	 * Sets the value of field 'language'.
	 * 
	 * @param language
	 *            the value of field 'language'.
	 */
	public void setLanguage(final String language) {
		this.language = language;
	}

	/**
	 * Sets the value of field 'lastName'.
	 * 
	 * @param lastName
	 *            the value of field 'lastName'.
	 */
	public void setLastName(final java.lang.String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Sets the value of field 'middleName'.
	 * 
	 * @param middleName
	 *            the value of field 'middleName'.
	 */
	public void setMiddleName(final java.lang.String middleName) {
		this.middleName = middleName;
	}

	/**
	 * Sets the value of field 'numberFormat'.
	 * 
	 * @param numberFormat
	 *            the value of field 'numberFormat'.
	 */
	public void setNumberFormat(final NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	/**
	 * Sets the value of field 'personalAddress'.
	 * 
	 * @param personalAddress
	 *            the value of field 'personalAddress'.
	 */
	public void setPersonalAddress(final PersonalAddress personalAddress) {
		this.personalAddress = personalAddress;
	}

	/**
	 * Sets the value of field 'timeFormat'.
	 * 
	 * @param timeFormat
	 *            the value of field 'timeFormat'.
	 */
	public void setTimeFormat(final SimpleDateFormat timeFormat) {
		this.timeFormat = timeFormat;
	}

	/**
	 * Sets the value of field 'timeZone'.
	 * 
	 * @param timeZone
	 *            the value of field 'timeZone'.
	 */
	public void setTimeZone(final TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Sets the value of field 'title'.
	 * 
	 * @param title
	 *            the value of field 'title'.
	 */
	public void setTitle(
			final eu.esdihumboldt.commons.mediator.usermanagement.types.impl.TitleType title) {
		this.title = title;
	}

	@Deprecated
	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	public List<X509Certificate> getX509Certificate() {
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
