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

package eu.esdihumboldt.specification.mediator.context;

import eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint;
import eu.esdihumboldt.specification.mediator.usermanagement.User;

/**
 * A UserContext Interface is a SubInterface of the DefaultContext and allows
 * access to the user context details like:
 * <ul>
 * <li>User, who is owner of this context,</li>
 * <li>Metadata Constraint for this user.</li>
 * </ul>
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: UserContext.java,v 1.3 2007-12-18 13:26:17 pitaeva Exp $
 * 
 */
public interface UserContext extends Context {

	/**
	 * 
	 * 
	 * @return the UserConstraint, which describes e.g.:
	 *         <ul>
	 *         <li>User Name,</li>
	 *         <li>User Adress Information.</li>
	 *         </ul>
	 * 
	 */
	public MetadataConstraint getUserConstraint();

	/**
	 * 
	 * 
	 * @return the System User, who is the owner of this context.
	 */
	public User getUser();

	/**
	 * Allows access to the ParentContext, that is a mandatory element for each
	 * UserContext. It contains all default constraints for the ChildContext.
	 * The User Default Constraints are defined for the Organization this user
	 * belongs to.
	 * 
	 * @return OrganizationContext parentContext.
	 */

	public OrganizationContext getParentContext();

}
