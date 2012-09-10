/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: CountryCodeType.java,v 1.3 2007-10-28 13:16:12 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.types.impl;

import java.util.Hashtable;

/**
 * It is a list of ISO-3166 CountryCodes.
 * 
 * TODO: extend this list.
 * 
 * 
 * @version $Revision: 1.3 $ $Date: 2007-10-28 13:16:12 $
 */
public class CountryCodeType implements java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * The GB type
	 */
	public static final int GB_TYPE = 0;

	/**
	 * The instance of the GB type
	 */
	public static final CountryCodeType GB = new CountryCodeType(GB_TYPE, "GB");

	/**
	 * The FR type
	 */
	public static final int FR_TYPE = 1;

	/**
	 * The instance of the FR type
	 */
	public static final CountryCodeType FR = new CountryCodeType(FR_TYPE, "FR");

	/**
	 * The DE type
	 */
	public static final int DE_TYPE = 2;

	/**
	 * The instance of the DE type
	 */
	public static final CountryCodeType DE = new CountryCodeType(DE_TYPE, "DE");

	/**
	 * The IT type
	 */
	public static final int IT_TYPE = 3;

	/**
	 * The instance of the IT type
	 */
	public static final CountryCodeType IT = new CountryCodeType(IT_TYPE, "IT");

	/**
	 * The ES type
	 */
	public static final int ES_TYPE = 4;

	/**
	 * The instance of the ES type
	 */
	public static final CountryCodeType ES = new CountryCodeType(ES_TYPE, "ES");

	/**
	 * Field _memberTable.
	 */
	private static java.util.Hashtable _memberTable = init();

	/**
	 * Field type.
	 */
	private int type;

	/**
	 * Field stringValue.
	 */
	private java.lang.String countryCode = null;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public CountryCodeType() {
	}

	public CountryCodeType(final int type, final java.lang.String value) {
		super();
		this.type = type;
		this.countryCode = value;
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method enumerate.Returns an enumeration of all possible instances of
	 * CountryCodeType
	 * 
	 * @return an Enumeration over all possible instances of CountryCodeType
	 */
	public static java.util.Enumeration enumerate() {
		return _memberTable.elements();
	}

	/**
	 * Method getType.Returns the type of this CountryCodeType
	 * 
	 * @return the type of this CountryCodeType
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
		members.put("GB", GB);
		members.put("FR", FR);
		members.put("DE", DE);
		members.put("IT", IT);
		members.put("ES", ES);
		return members;
	}

	/**
	 * Method readResolve. will be called during deserialization to replace the
	 * deserialized object with the correct constant instance.
	 * 
	 * @return this deserialized object
	 */
	private java.lang.Object readResolve() {
		return valueOf(this.countryCode);
	}

	/**
	 * Method toString.Returns the String representation of this CountryCodeType
	 * 
	 * @return the String representation of this CountryCodeType
	 */
	public java.lang.String toString() {
		return this.countryCode;
	}

	/**
	 * Method valueOf.Returns a new CountryCodeType based on the given String
	 * value.
	 * 
	 * @param string
	 * @return the CountryCodeType value of parameter 'string'
	 */
	public static eu.esdihumboldt.commons.mediator.usermanagement.types.impl.CountryCodeType valueOf(
			final java.lang.String string) {
		java.lang.Object obj = null;
		if (string != null) {
			obj = _memberTable.get(string);
		}
		if (obj == null) {
			String err = "" + string + " is not a valid CountryCodeType";
			throw new IllegalArgumentException(err);
		}
		return (CountryCodeType) obj;
	}

	public java.lang.String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(java.lang.String stringValue) {
		this.countryCode = stringValue;
	}

}
