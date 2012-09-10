/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: AcademicTitleType.java,v 1.4 2007-11-13 09:01:24 jamess Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.types.impl;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Class AcademicTitleType.
 * 
 * @version $Revision: 1.4 $ $Date: 2007-11-13 09:01:24 $
 */
public class AcademicTitleType implements java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * The Prof. type
	 */
	public static final int PROF__TYPE = 0;

	/**
	 * The instance of the Prof. type
	 */
	public static final AcademicTitleType PROF_ = new AcademicTitleType(
			PROF__TYPE, "Prof.");

	/**
	 * The Ph.D. type
	 */
	public static final int PH_D__TYPE = 1;

	/**
	 * The instance of the Ph.D. type
	 */
	public static final AcademicTitleType PH_D_ = new AcademicTitleType(
			PH_D__TYPE, "Ph.D.");

	/**
	 * The Master type
	 */
	public static final int MASTER_TYPE = 2;

	/**
	 * The instance of the Master type
	 */
	public static final AcademicTitleType MASTER = new AcademicTitleType(
			MASTER_TYPE, "Master");

	/**
	 * The Bachelor type
	 */
	public static final int BACHELOR_TYPE = 3;

	/**
	 * The instance of the Bachelor type
	 */
	public static final AcademicTitleType BACHELOR = new AcademicTitleType(
			BACHELOR_TYPE, "Bachelor");

	/**
	 * The Other type
	 */
	public static final int OTHER_TYPE = 4;

	/**
	 * The instance of the Other type
	 */
	public static final AcademicTitleType OTHER = new AcademicTitleType(
			OTHER_TYPE, "Other");

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
	private java.lang.String stringValue = null;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public AcademicTitleType() {
	}

	public AcademicTitleType(final int type, final java.lang.String value) {
		super();
		this.type = type;
		this.stringValue = value;
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method enumerate.Returns an enumeration of all possible instances of
	 * AcademicTitleType
	 * 
	 * @return an Enumeration over all possible instances of AcademicTitleType
	 */
	public static java.util.Enumeration enumerate() {
		return _memberTable.elements();
	}

	/**
	 * Method getType.Returns the type of this AcademicTitleType
	 * 
	 * @return the type of this AcademicTitleType
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
		members.put("Prof.", PROF_);
		members.put("Ph.D.", PH_D_);
		members.put("Master", MASTER);
		members.put("Bachelor", BACHELOR);
		members.put("Other", OTHER);
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
	 * AcademicTitleType
	 * 
	 * @return the String representation of this AcademicTitleType
	 */
	public java.lang.String toString() {
		return this.stringValue;
	}

	/**
	 * Method valueOf.Returns a new AcademicTitleType based on the given String
	 * value.
	 * 
	 * @param string
	 * @return the AcademicTitleType value of parameter 'string'
	 */
	public static eu.esdihumboldt.commons.mediator.usermanagement.types.impl.AcademicTitleType valueOf(
			final java.lang.String string) {
		java.lang.Object obj = null;
		if (string != null) {
			obj = _memberTable.get(string);
		}
		if (obj == null) {
			String err = "" + string + " is not a valid AcademicTitleType";
			throw new IllegalArgumentException(err);
		}
		return (AcademicTitleType) obj;
	}

	public java.lang.String getStringValue() {
		return stringValue;
	}

	public void setStringValue(java.lang.String stringValue) {
		this.stringValue = stringValue;
	}

}
