/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator.usermanagement;

import java.util.UUID;

/**
 * An Address Interface provides all the address information for a
 * user/organization.
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: Address.java,v 1.4 2007-11-09 07:35:04 pitaeva Exp $
 * 
 */

public interface Address {

	/**
	 * 
	 * @return OrganizationAddressID that can be used in place of the address
	 *         information to reference an organizations address by the ID
	 *         assigned to it.
	 */
	public UUID getAddressID();

	/**
	 * 
	 * @return POBox-Element, that contains the post office box number.
	 */
	public String getPOBox();

	/**
	 * 
	 * @return the street name (inline house number allowed). Either Street or
	 *         POBox must be represented.
	 */
	public String getStreet();

	/**
	 * 
	 * @return the house number or range of numbers.
	 */
	public String getHouseNumber();

	/**
	 * 
	 * @return StreetSupplement1, that contains any additional information to
	 *         identify the street.
	 */
	public String getStreetSupplement1();

	/**
	 * 
	 * @return StreetSupplement2, that contains any further additional
	 *         information to identify the street.
	 */
	public String getStreetSupplement2();

	/**
	 * 
	 * @return PostalCode,that contains the identifier for one or more
	 *         properties according to the postal service of that country.
	 */
	public String getPostalCode();

	/**
	 * 
	 * @return the name of the city.
	 */
	public String getCity();

	/**
	 * 
	 * @return the name of the county.
	 */
	public String getCounty();

	/**
	 * 
	 * @return the name of the district. This element is only used for those
	 *         countries that have these sub-elements.
	 */
	public String getDistrict();

	/**
	 * 
	 * @return the ISO-Code of country.
	 */
	public CountryCode getCountry();

	/**
	 * 
	 * This Type contains a List of the ISO-3166 defined CountryCodes.
	 * 
	 */
	public enum CountryCode {
		/** Country code specified in the ISO-3166 document */
		ISO3166Code
	}
}
