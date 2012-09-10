/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: PersonProfileImpl.java,v 1.1 2007-10-19 10:03:07 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class PersonProfile.
 * 
 * @version $Revision: 1.1 $ $Date: 2007-10-19 10:03:07 $
 */
public class PersonProfileImpl extends PersonProfileType implements
		java.io.Serializable {

	// ----------------/
	// - Constructors -/
	// ----------------/

	public PersonProfileImpl() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * implementation of org.castor.xmlctf.CastorTestable
	 * 
	 * @return a String representation of all of the fields for
	 *         eu.esdihumboldt.mediator.usermanagement.PersonProfile
	 */
	public java.lang.String dumpFields() {
		StringBuffer result = new StringBuffer(
				"DumpFields() for element: eu.esdihumboldt.mediator.usermanagement.PersonProfile\n");

		return result.toString();
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
	 * 
	 * 
	 * @param out
	 * @throws org.exolab.castor.xml.MarshalException
	 *             if object is null or if any SAXException is thrown during
	 *             marshaling
	 * @throws org.exolab.castor.xml.ValidationException
	 *             if this object is an invalid instance according to the schema
	 */
	public void marshal(final java.io.Writer out)
			throws org.exolab.castor.xml.MarshalException,
			org.exolab.castor.xml.ValidationException {
		Marshaller.marshal(this, out);
	}

	/**
	 * 
	 * 
	 * @param handler
	 * @throws java.io.IOException
	 *             if an IOException occurs during marshaling
	 * @throws org.exolab.castor.xml.ValidationException
	 *             if this object is an invalid instance according to the schema
	 * @throws org.exolab.castor.xml.MarshalException
	 *             if object is null or if any SAXException is thrown during
	 *             marshaling
	 */
	public void marshal(final org.xml.sax.ContentHandler handler)
			throws java.io.IOException, org.exolab.castor.xml.MarshalException,
			org.exolab.castor.xml.ValidationException {
		Marshaller.marshal(this, handler);
	}

	/**
	 * implementation of org.castor.xmlctf.CastorTestable
	 * 
	 * @throws InstantiationException
	 *             if we try to instantiate an abstract class or interface
	 * @throws IllegalAccessException
	 *             if we do not have access to the field, for example if it is
	 *             private
	 */
	public void randomizeFields() throws InstantiationException,
			IllegalAccessException {
	}

	/**
	 * Method unmarshal.
	 * 
	 * @param reader
	 * @throws org.exolab.castor.xml.MarshalException
	 *             if object is null or if any SAXException is thrown during
	 *             marshaling
	 * @throws org.exolab.castor.xml.ValidationException
	 *             if this object is an invalid instance according to the schema
	 * @return the unmarshaled
	 *         eu.esdihumboldt.mediator.usermanagement.PersonProfileType
	 */
	public static eu.esdihumboldt.commons.mediator.usermanagement.impl.PersonProfileType unmarshal(
			final java.io.Reader reader)
			throws org.exolab.castor.xml.MarshalException,
			org.exolab.castor.xml.ValidationException {
		return (eu.esdihumboldt.commons.mediator.usermanagement.impl.PersonProfileType) Unmarshaller
				.unmarshal(
						eu.esdihumboldt.commons.mediator.usermanagement.impl.PersonProfileImpl.class,
						reader);
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
