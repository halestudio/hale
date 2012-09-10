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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.context.Context;

/**
 * An Organizatin Interface allows access to the all organization attributes
 * like:
 * 
 * <ul>
 * <li>Organization Name</li>
 * <li>Organization Address,</li>
 * <li>List of User.</li>
 * </ul>
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: Organization.java,v 1.4 2007-11-14 10:32:37 jamess Exp $
 * 
 */
public interface Organization {

	/**
	 * 
	 * @return an unique idetifier of the organization.
	 */
	public UUID getOrganizationID();

	/**
	 * 
	 * @return the dates the trading partner organization is valid within the
	 *         marketplace.
	 */
	@Deprecated
	public ValidatyDates getValidatyDates();

	/**
	 * 
	 * @return the name displayed in the application's user-interface.
	 */

	public String getOrganizationDisplayName();

	/**
	 * @return the first name line of an organization.
	 */
	public String getNameLine1();

	/**
	 * @return the second name line of an organization.
	 */
	public String getNameLine2();

	/**
	 * @return the third name line of an organization.
	 */
	public String getNameLine3();

	/**
	 * 
	 * @return the main communication language of the organization.
	 */

	public Object getLanguage();

	/**
	 * @return the home-currency of the organization. It corresponds to the main
	 *         general ledger currency of the organization.
	 * @Deprecated too complex for the prototype needs, can be used for the
	 *             Production
	 */
	@Deprecated
	public Currency getCurrency();

	/**
	 * 
	 * The Currency identifies a currency of the organization.
	 * 
	 * @Deprecated too complex for the prototype needs, can be used for the
	 *             Production
	 * 
	 */
	@Deprecated
	public enum Currency {
		/**
		 * identifies a currency using the standard set by UN/ECE Recommendation
		 * no.9 .
		 */
		CurrencyCoded,
		/**
		 * used to provide a non-standard CurrencyCode. This element is
		 * mandatory if the value of CurrencyCoded is 'Other'.
		 */
		CurrencyCodedOther
	}

	/**
	 * 
	 * @return any free-form text pertinent to the entire organization.
	 */
	public String getGeneralNotes();

	/**
	 * 
	 * @return the List of users of this organization.
	 */
	public List<User> getUserList();

	/**
	 * 
	 * @return the List of addresses for this organization in descending order
	 *         of priority. The Address on position 0 has the highest, the
	 *         Address on position n-1 the lowest priority.
	 */

	public List<OrganizationAddress> getOrganizationAddressList();

	/**
	 * @return a List of Organization Contexts, in descending order of priority.
	 *         The Context on position 0 has the highest, the Context on
	 *         position n-1 the lowest priority.
	 * 
	 */
	public List<Context> getContextList();

	/**
	 * 
	 * @return the List of banking details for this organization.
	 * @Deprecated too complex for the prototype needs, can be used for the
	 *             Production
	 */
	@Deprecated
	public Set<BankDetail> getBankDetail();

	/**
	 * contains the banking details of the organization.
	 * 
	 * @Deprecated too complex for the prototype needs, can be used for the
	 *             Production
	 */
	@Deprecated
	public enum BankDetail {
		/** holds the ISO-Code of the country. */
		BankCountry,
		/** the national bank key. */
		BankKey,
		/** holds the SWIFT Bank ID. */
		SWIFTCode,
		/** contains the Account number for the organization. */
		BankAccountNumber,
		/**
		 * contains the IBAN (International Bank Account Number) for the
		 * organization.
		 */
		InternationalBankAccountNumber,
		/** contains the account holder for the organization. */
		TradingPartnerAccountHolder,
		/** contains the control key for the bank account. */
		BankAccountControlKey,
		/** contains bank detail reference information for this account. */
		BankReference
	}

}
