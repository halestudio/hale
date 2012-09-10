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

/**
 * A UserAddress contains a user-specific specified address within an
 * Organization. Organization address can be referenced as an option.Personal
 * addresses that do not exist as corporate addresses will not be stored. Any
 * address that is associated with a user is also a corporate address.
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: UserAddress.java,v 1.2 2007-11-09 07:35:23 pitaeva Exp $
 * 
 */

public interface UserAddress extends Address {

	/**
	 * 
	 * @return the name of the building.
	 */

	public String getBuilding();

	/**
	 * 
	 * @return the number of the floor.
	 */
	public String getFloor();

	/**
	 * 
	 * @return the identifier for the room.
	 */
	public String getRoomNumber();

	/**
	 * 
	 * @return the inhouse mail identifier.
	 */

	public String getInhouseMail();

	/**
	 * 
	 * @return the department number or name.
	 */

	public String getDepartment();

	/**
	 * 
	 * @return the List of CommunicationDetails.
	 */
	public List<CommunicationDetail> getCommunicationDetail();

	/**
	 * 
	 * A CommunicationDetail is the information of the communication detail for
	 * the person.
	 * 
	 */
	public enum CommunicationDetail {
		/**
		 * Contains a description of the communication detail for the person,
		 * e.g. telephone number.
		 */
		CommunicationDetailDescription,
		/** Contains the information on the communication method. */
		PersonCommunicationType,
		/** Contains the communication number or identifier */
		CommunicationValue,
		/**
		 * A flag to provide information on whether this is the default
		 * communication method.
		 */
		DefaultCommunication

	}
}
