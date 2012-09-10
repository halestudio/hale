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

import java.util.TimeZone;

/**
 * A OrganizationAddress contains all relevant address inforamtion for an
 * organization.
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: OrganizationAddress.java,v 1.1 2007-10-19 10:03:07 pitaeva Exp
 *          $
 * 
 */
public interface OrganizationAddress extends Address {

	/**
	 * 
	 * @return the function of the address.
	 * @Deprecated too complex for the prototype needs, can be used for the
	 *             Production
	 */
	@Deprecated
	public AddressType getAddressType();

	/**
	 * 
	 * The AddressType is the container used to specify the function of an
	 * address.
	 * 
	 * @Deprecated too complex for the prototype needs, can be used for the
	 *             Production
	 */
	@Deprecated
	public enum AddressType {

		/**
		 * specifies the function of the address using a standard codelist. see
		 * EDIFACT 3035 (Party Function Code Qualifier)
		 */
		AddressTypeCoded,
		/**
		 * is used to provide a non-standard AddressTypeCode. This element is
		 * mandatory if the value of AddressTypeCoded is 'Other'.
		 */
		AddressTypeCodedOther

	}

	/**
	 * 
	 * @return the off-set from Greenwich Mean Time that the organization
	 *         operates to.
	 * 
	 */
	public TimeZone getTimeZone();

}
