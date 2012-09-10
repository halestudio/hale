/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: OrganizationAddressImpl.java,v 1.3 2007-11-13 09:02:02 jamess Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

//---------------------------------/
//- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class OrganizationAddress.
 * 
 * @version $Revision: 1.3 $ $Date: 2007-11-13 09:02:02 $
 */
public class OrganizationAddressImpl extends OrganizationAddressType implements
		java.io.Serializable {

	// ----------------/
	// - Constructors -/
	// ----------------/

	public OrganizationAddressImpl() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

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
	 * Method unmarshal.
	 * 
	 * @param reader
	 * @throws org.exolab.castor.xml.MarshalException
	 *             if object is null or if any SAXException is thrown during
	 *             marshaling
	 * @throws org.exolab.castor.xml.ValidationException
	 *             if this object is an invalid instance according to the schema
	 * @return the unmarshaled
	 *         eu.esdihumboldt.mediator.usermanagement.AddressType
	 */
	public static eu.esdihumboldt.commons.mediator.usermanagement.impl.AddressType unmarshal(
			final java.io.Reader reader)
			throws org.exolab.castor.xml.MarshalException,
			org.exolab.castor.xml.ValidationException {
		return (eu.esdihumboldt.commons.mediator.usermanagement.impl.AddressType) Unmarshaller
				.unmarshal(
						eu.esdihumboldt.commons.mediator.usermanagement.impl.OrganizationAddressImpl.class,
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

	public AddressType getAddressType() {
		// TODO Auto-generated method stub
		return null;
	}

}
