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
 * A Role represents the role that the actual user has within the HUMBOLDT
 * system, not his role to the outside world. It is therefore only of concern
 * when internal permissions are checked.
 * 
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: Role.java,v 1.3 2007-10-24 13:42:53 pitaeva Exp $
 * 
 */
public interface Role {

	/**
	 * 
	 * @return the role of the user.
	 */
	public String getRoleName();

	/**
	 * 
	 * @return an uniquie identification assigned to the user role.
	 */
	public UUID getRoleID();
}
