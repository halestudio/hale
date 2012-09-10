/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: LanguageType.java,v 1.3 2007-10-26 12:55:40 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.types.impl;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * It is a list of ISO 639-1 LanguageCodes.
 * 
 * TODO: extend this list.
 * 
 * 
 * @version $Revision: 1.3 $ $Date: 2007-10-26 12:55:40 $
 */
public class LanguageType implements java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * The en type
	 */
	public static final int EN_TYPE = 0;

	/**
	 * The instance of the en type
	 */
	public static final LanguageType EN = new LanguageType(EN_TYPE, "en");

	/**
	 * The fr type
	 */
	public static final int FR_TYPE = 1;

	/**
	 * The instance of the fr type
	 */
	public static final LanguageType FR = new LanguageType(FR_TYPE, "fr");

	/**
	 * The de type
	 */
	public static final int DE_TYPE = 2;

	/**
	 * The instance of the de type
	 */
	public static final LanguageType DE = new LanguageType(DE_TYPE, "de");

	/**
	 * The it type
	 */
	public static final int IT_TYPE = 3;

	/**
	 * The instance of the it type
	 */
	public static final LanguageType IT = new LanguageType(IT_TYPE, "it");

	/**
	 * The es type
	 */
	public static final int ES_TYPE = 4;

	/**
	 * The instance of the es type
	 */
	public static final LanguageType ES = new LanguageType(ES_TYPE, "es");

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
	private java.lang.String language;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public LanguageType() {
	};

	public LanguageType(final int type, final java.lang.String value) {
		super();
		this.type = type;
		this.language = value;
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method enumerate.Returns an enumeration of all possible instances of
	 * LanguageType
	 * 
	 * @return an Enumeration over all possible instances of LanguageType
	 */
	public static java.util.Enumeration enumerate() {
		return _memberTable.elements();
	}

	/**
	 * Method getType.Returns the type of this LanguageType
	 * 
	 * @return the type of this LanguageType
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
		members.put("en", EN);
		members.put("fr", FR);
		members.put("de", DE);
		members.put("it", IT);
		members.put("es", ES);
		return members;
	}

	/**
	 * Method readResolve. will be called during deserialization to replace the
	 * deserialized object with the correct constant instance.
	 * 
	 * @return this deserialized object
	 */
	private java.lang.Object readResolve() {
		return valueOf(this.language);
	}

	/**
	 * Method toString.Returns the String representation of this LanguageType
	 * 
	 * @return the String representation of this LanguageType
	 */
	public java.lang.String toString() {
		return this.language;
	}

	/**
	 * Method valueOf.Returns a new LanguageType based on the given String
	 * value.
	 * 
	 * @param string
	 * @return the LanguageType value of parameter 'string'
	 */
	public static eu.esdihumboldt.commons.mediator.usermanagement.types.impl.LanguageType valueOf(
			final java.lang.String string) {
		java.lang.Object obj = null;
		if (string != null) {
			obj = _memberTable.get(string);
		}
		if (obj == null) {
			String err = "" + string + " is not a valid LanguageType";
			throw new IllegalArgumentException(err);
		}
		return (LanguageType) obj;
	}

	public java.lang.String getLanguage() {
		return language;
	}

	public void setLanguage(java.lang.String language) {
		this.language = language;
	}

}
