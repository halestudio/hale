/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: PersonCommunicationTypeCodedType.java,v 1.2 2007-10-23 10:09:36 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.types.impl;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * This code identifies the communication type of the person involved.
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2007-10-23 10:09:36 $
 */
public class PersonCommunicationTypeCodedType implements java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * The Other type
	 */
	public static final int OTHER_TYPE = 0;

	/**
	 * The instance of the Other type
	 */
	public static final PersonCommunicationTypeCodedType OTHER = new PersonCommunicationTypeCodedType(
			OTHER_TYPE, "Other");

	/**
	 * The TelephoneNumber type
	 */
	public static final int TELEPHONENUMBER_TYPE = 1;

	/**
	 * The instance of the TelephoneNumber type
	 */
	public static final PersonCommunicationTypeCodedType TELEPHONENUMBER = new PersonCommunicationTypeCodedType(
			TELEPHONENUMBER_TYPE, "TelephoneNumber");

	/**
	 * The FaxNumber type
	 */
	public static final int FAXNUMBER_TYPE = 2;

	/**
	 * The instance of the FaxNumber type
	 */
	public static final PersonCommunicationTypeCodedType FAXNUMBER = new PersonCommunicationTypeCodedType(
			FAXNUMBER_TYPE, "FaxNumber");

	/**
	 * The EmailAddress type
	 */
	public static final int EMAILADDRESS_TYPE = 3;

	/**
	 * The instance of the EmailAddress type
	 */
	public static final PersonCommunicationTypeCodedType EMAILADDRESS = new PersonCommunicationTypeCodedType(
			EMAILADDRESS_TYPE, "EmailAddress");

	/**
	 * The MobileNumber type
	 */
	public static final int MOBILENUMBER_TYPE = 4;

	/**
	 * The instance of the MobileNumber type
	 */
	public static final PersonCommunicationTypeCodedType MOBILENUMBER = new PersonCommunicationTypeCodedType(
			MOBILENUMBER_TYPE, "MobileNumber");

	/**
	 * The HomePage type
	 */
	public static final int HOMEPAGE_TYPE = 5;

	/**
	 * The instance of the HomePage type
	 */
	public static final PersonCommunicationTypeCodedType HOMEPAGE = new PersonCommunicationTypeCodedType(
			HOMEPAGE_TYPE, "HomePage");

	/**
	 * The URL type
	 */
	public static final int URL_TYPE = 6;

	/**
	 * The instance of the URL type
	 */
	public static final PersonCommunicationTypeCodedType URL = new PersonCommunicationTypeCodedType(
			URL_TYPE, "URL");

	/**
	 * The FTP type
	 */
	public static final int FTP_TYPE = 7;

	/**
	 * The instance of the FTP type
	 */
	public static final PersonCommunicationTypeCodedType FTP = new PersonCommunicationTypeCodedType(
			FTP_TYPE, "FTP");

	/**
	 * Field _memberTable.
	 */
	private static java.util.Hashtable _memberTable = init();

	/**
	 * Field type.
	 */
	private final int type;

	/**
	 * Field stringValue.
	 */
	private java.lang.String stringValue = null;

	// ----------------/
	// - Constructors -/
	// ----------------/

	private PersonCommunicationTypeCodedType(final int type,
			final java.lang.String value) {
		super();
		this.type = type;
		this.stringValue = value;
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method enumerate.Returns an enumeration of all possible instances of
	 * PersonCommunicationTypeCodedType
	 * 
	 * @return an Enumeration over all possible instances of
	 *         PersonCommunicationTypeCodedType
	 */
	public static java.util.Enumeration enumerate() {
		return _memberTable.elements();
	}

	/**
	 * Method getType.Returns the type of this PersonCommunicationTypeCodedType
	 * 
	 * @return the type of this PersonCommunicationTypeCodedType
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Method init.
	 * 
	 * @return the initialized Hashtable for the member table
	 */
	private static java.util.Hashtable init() {
		Hashtable members = new Hashtable();
		members.put("Other", OTHER);
		members.put("TelephoneNumber", TELEPHONENUMBER);
		members.put("FaxNumber", FAXNUMBER);
		members.put("EmailAddress", EMAILADDRESS);
		members.put("MobileNumber", MOBILENUMBER);
		members.put("HomePage", HOMEPAGE);
		members.put("URL", URL);
		members.put("FTP", FTP);
		return members;
	}

	/**
	 * Method readResolve. will be called during deserialization to replace the
	 * deserialized object with the correct constant instance.
	 * 
	 * @return this deserialized object
	 */
	private java.lang.Object readResolve() {
		return valueOf(this.stringValue);
	}

	/**
	 * Method toString.Returns the String representation of this
	 * PersonCommunicationTypeCodedType
	 * 
	 * @return the String representation of this
	 *         PersonCommunicationTypeCodedType
	 */
	public java.lang.String toString() {
		return this.stringValue;
	}

	/**
	 * Method valueOf.Returns a new PersonCommunicationTypeCodedType based on
	 * the given String value.
	 * 
	 * @param string
	 * @return the PersonCommunicationTypeCodedType value of parameter 'string'
	 */
	public static eu.esdihumboldt.commons.mediator.usermanagement.types.impl.PersonCommunicationTypeCodedType valueOf(
			final java.lang.String string) {
		java.lang.Object obj = null;
		if (string != null) {
			obj = _memberTable.get(string);
		}
		if (obj == null) {
			String err = "" + string
					+ " is not a valid PersonCommunicationTypeCodedType";
			throw new IllegalArgumentException(err);
		}
		return (PersonCommunicationTypeCodedType) obj;
	}

	public java.lang.String getStringValue() {
		return stringValue;
	}

	public void setStringValue(java.lang.String stringValue) {
		this.stringValue = stringValue;
	}

}
