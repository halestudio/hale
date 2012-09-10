/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
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
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.context.Context;

/**
 * A User Interface allows access to the user's attributes like:
 * <ul>
 * <li>User Name</li>
 * <li>User Address,</li>
 * <li>User User Role.</li>
 * </ul>
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: User.java,v 1.3 2007-10-24 13:42:53 pitaeva Exp $
 * 
 */
public interface User {

	/**
	 * 
	 * @return a unigue user identifier within the HUMBOLD System.
	 */
	UUID getUserID();

	/**
	 * @return a List of User Contexts, in descending order of priority. The
	 *         Context on position 0 has the highest, the Context on position
	 *         n-1 the lowest priority.
	 * 
	 */
	public List<Context> getContextList();

	/**
	 * 
	 * @return the user role within the HUMBOLDT System.
	 */

	public Role getInternalRole();

	/**
	 * 
	 * @return a Organization Reference for this user.
	 * 
	 */
	public UUID getOrganizationID();

	/**
	 * 
	 * @return a PersonProfile for this user.
	 * 
	 */

	public PersonProfile getPersonProfile();

	/**
	 * 
	 * @return a period in which the user is valid.
	 * @Deprecated too complex for the prototype needs, can be used for the
	 *             Production
	 */
	@Deprecated
	public ValidatyDates getValidityDates();
}
