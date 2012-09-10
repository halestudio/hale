/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: TitleType.java,v 1.3 2007-10-28 13:16:12 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.types.impl;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Class TitleType.
 * 
 * @version $Revision: 1.3 $ $Date: 2007-10-28 13:16:12 $
 */
public class TitleType implements java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * The Mrs. type
	 */
	public static final int MRS__TYPE = 0;

	/**
	 * The instance of the Mrs. type
	 */
	public static final TitleType MRS_ = new TitleType(MRS__TYPE, "Mrs.");

	/**
	 * The Ms. type
	 */
	public static final int MS__TYPE = 1;

	/**
	 * The instance of the Ms. type
	 */
	public static final TitleType MS_ = new TitleType(MS__TYPE, "Ms.");

	/**
	 * The Mr. type
	 */
	public static final int MR__TYPE = 2;

	/**
	 * The instance of the Mr. type
	 */
	public static final TitleType MR_ = new TitleType(MR__TYPE, "Mr.");

	/**
	 * The Other type
	 */
	public static final int OTHER_TYPE = 3;

	/**
	 * The instance of the Other type
	 */
	public static final TitleType OTHER = new TitleType(OTHER_TYPE, "Other");

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

	public TitleType() {
	}

	public TitleType(final int type, final java.lang.String value) {
		super();
		this.type = type;
		this.stringValue = value;
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method enumerate.Returns an enumeration of all possible instances of
	 * TitleType
	 * 
	 * @return an Enumeration over all possible instances of TitleType
	 */
	public static java.util.Enumeration enumerate() {
		return _memberTable.elements();
	}

	/**
	 * Method getType.Returns the type of this TitleType
	 * 
	 * @return the type of this TitleType
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
		members.put("Mrs.", MRS_);
		members.put("Ms.", MS_);
		members.put("Mr.", MR_);
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
	 * Method toString.Returns the String representation of this TitleType
	 * 
	 * @return the String representation of this TitleType
	 */
	public java.lang.String toString() {
		return this.stringValue;
	}

	/**
	 * Method valueOf.Returns a new TitleType based on the given String value.
	 * 
	 * @param string
	 * @return the TitleType value of parameter 'string'
	 */
	public static eu.esdihumboldt.commons.mediator.usermanagement.types.impl.TitleType valueOf(
			final java.lang.String string) {
		java.lang.Object obj = null;
		if (string != null) {
			obj = _memberTable.get(string);
		}
		if (obj == null) {
			String err = "" + string + " is not a valid TitleType";
			throw new IllegalArgumentException(err);
		}
		return (TitleType) obj;
	}

	public java.lang.String getStringValue() {
		return stringValue;
	}

	public void setStringValue(java.lang.String stringValue) {
		this.stringValue = stringValue;
	}

}
