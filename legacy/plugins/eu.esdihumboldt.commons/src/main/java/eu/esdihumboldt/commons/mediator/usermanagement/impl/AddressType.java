/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: AddressType.java,v 1.3 2007-10-24 13:42:53 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import java.util.UUID;

import eu.esdihumboldt.specification.mediator.usermanagement.Address;

/**
 * This abstract type stores the information, that is common for the UserAddress
 * and OrganizationAddress.
 * 
 * 
 * @version $Revision: 1.3 $ $Date: 2007-10-24 13:42:53 $
 */
public abstract class AddressType implements java.io.Serializable, Address {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * Field _organizationAddressId.
	 */
	private UUID addressID;

	// private UUID organizationAddressId;

	/**
	 * Field _POBox.
	 */
	private String POBox;

	/**
	 * keeps track of state for field: _POBox
	 */
	private boolean has_POBox;

	/**
	 * Field _street.
	 */
	private java.lang.String street;

	/**
	 * Field _houseNumber.
	 */
	private String houseNumber;

	/**
	 * keeps track of state for field: _houseNumber
	 */
	private boolean has_houseNumber;

	/**
	 * Field _streetSupplement1.
	 */
	private java.lang.String streetSupplement1;

	/**
	 * Field _streetSupplement2.
	 */
	private java.lang.String streetSupplement2;

	/**
	 * Field _postalCode.
	 */
	private java.lang.String postalCode;

	/**
	 * Field _city.
	 */
	private java.lang.String city;

	/**
	 * Field _county.
	 */
	private java.lang.String county;

	/**
	 * Field _district.
	 */
	private java.lang.String district;

	/**
	 * Field _countryCode.
	 */
	private eu.esdihumboldt.commons.mediator.usermanagement.types.impl.CountryCodeType countryCode;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public AddressType() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
     */
	public void deleteHouseNumber() {
		this.has_houseNumber = false;
	}

	/**
     */
	public void deletePOBox() {
		this.has_POBox = false;
	}

	/**
	 * Returns the value of field 'city'.
	 * 
	 * @return the value of field 'City'.
	 */
	public java.lang.String getCity() {
		return this.city;
	}

	/**
	 * Returns the value of field 'countryCode'.
	 * 
	 * @return the value of field 'CountryCode'.
	 */
	public eu.esdihumboldt.commons.mediator.usermanagement.types.impl.CountryCodeType getCountryCode() {
		return this.countryCode;
	}

	/**
	 * Returns the value of field 'county'.
	 * 
	 * @return the value of field 'County'.
	 */
	public java.lang.String getCounty() {
		return this.county;
	}

	/**
	 * Returns the value of field 'district'.
	 * 
	 * @return the value of field 'District'.
	 */
	public java.lang.String getDistrict() {
		return this.district;
	}

	/**
	 * Returns the value of field 'houseNumber'.
	 * 
	 * @return the value of field 'HouseNumber'.
	 */
	public String getHouseNumber() {
		return this.houseNumber;
	}

	/**
	 * Returns the value of field 'AddressId'.
	 * 
	 * @return the value of field 'AddressId'.
	 */
	public UUID getAddressID() {
		return this.addressID;
	}

	/**
	 * Returns the value of field 'POBox'.
	 * 
	 * @return the value of field 'POBox'.
	 */
	public String getPOBox() {
		return this.POBox;
	}

	/**
	 * Returns the value of field 'postalCode'.
	 * 
	 * @return the value of field 'PostalCode'.
	 */
	public java.lang.String getPostalCode() {
		return this.postalCode;
	}

	/**
	 * Returns the value of field 'street'.
	 * 
	 * @return the value of field 'Street'.
	 */
	public java.lang.String getStreet() {
		return this.street;
	}

	/**
	 * Returns the value of field 'streetSupplement1'.
	 * 
	 * @return the value of field 'StreetSupplement1'.
	 */
	public java.lang.String getStreetSupplement1() {
		return this.streetSupplement1;
	}

	/**
	 * Returns the value of field 'streetSupplement2'.
	 * 
	 * @return the value of field 'StreetSupplement2'.
	 */
	public java.lang.String getStreetSupplement2() {
		return this.streetSupplement2;
	}

	/**
	 * Method hasHouseNumber.
	 * 
	 * @return true if at least one HouseNumber has been added
	 */
	public boolean hasHouseNumber() {
		return this.has_houseNumber;
	}

	/**
	 * Method hasPOBox.
	 * 
	 * @return true if at least one POBox has been added
	 */
	public boolean hasPOBox() {
		return this.has_POBox;
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
	 * Sets the value of field 'city'.
	 * 
	 * @param city
	 *            the value of field 'city'.
	 */
	public void setCity(final java.lang.String city) {
		this.city = city;
	}

	/**
	 * Sets the value of field 'countryCode'.
	 * 
	 * @param countryCode
	 *            the value of field 'countryCode'.
	 */
	public void setCountryCode(
			final eu.esdihumboldt.commons.mediator.usermanagement.types.impl.CountryCodeType countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Sets the value of field 'county'.
	 * 
	 * @param county
	 *            the value of field 'county'.
	 */
	public void setCounty(final java.lang.String county) {
		this.county = county;
	}

	/**
	 * Sets the value of field 'district'.
	 * 
	 * @param district
	 *            the value of field 'district'.
	 */
	public void setDistrict(final java.lang.String district) {
		this.district = district;
	}

	/**
	 * Sets the value of field 'houseNumber'.
	 * 
	 * @param houseNumber
	 *            the value of field 'houseNumber'.
	 */
	public void setHouseNumber(final String houseNumber) {
		this.houseNumber = houseNumber;
		this.has_houseNumber = true;
	}

	/**
	 * Sets the value of field 'organizationAddressId'.
	 * 
	 * @param organizationAddressId
	 *            the value of field 'organizationAddressId'.
	 */
	public void setAddressID(final java.util.UUID addressId) {
		this.addressID = addressId;
	}

	/**
	 * Sets the value of field 'POBox'.
	 * 
	 * @param POBox
	 *            the value of field 'POBox'.
	 */
	public void setPOBox(final String POBox) {
		this.POBox = POBox;
		this.has_POBox = true;
	}

	/**
	 * Sets the value of field 'postalCode'.
	 * 
	 * @param postalCode
	 *            the value of field 'postalCode'.
	 */
	public void setPostalCode(final java.lang.String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * Sets the value of field 'street'.
	 * 
	 * @param street
	 *            the value of field 'street'.
	 */
	public void setStreet(final java.lang.String street) {
		this.street = street;
	}

	/**
	 * Sets the value of field 'streetSupplement1'.
	 * 
	 * @param streetSupplement1
	 *            the value of field 'streetSupplement1'.
	 */
	public void setStreetSupplement1(final java.lang.String streetSupplement1) {
		this.streetSupplement1 = streetSupplement1;
	}

	/**
	 * Sets the value of field 'streetSupplement2'.
	 * 
	 * @param streetSupplement2
	 *            the value of field 'streetSupplement2'.
	 */
	public void setStreetSupplement2(final java.lang.String streetSupplement2) {
		this.streetSupplement2 = streetSupplement2;
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

	// public UUID getOrganizationAddressID() {
	// return organizationAddressId;
	// }

	// public void setOrganizationAddressID(UUID addressId) {
	// organizationAddressId = addressId;
	// }

}
