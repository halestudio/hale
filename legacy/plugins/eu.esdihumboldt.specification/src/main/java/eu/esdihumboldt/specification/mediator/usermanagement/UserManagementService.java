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

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.esdihumboldt.specification.mediator.usermanagement.exceptions.InconsistentOrganizationConstraintException;
import eu.esdihumboldt.specification.mediator.usermanagement.exceptions.InconsistentUserConstraintException;
import eu.esdihumboldt.specification.mediator.usermanagement.exceptions.OrganizationNotFoundException;
import eu.esdihumboldt.specification.mediator.usermanagement.exceptions.UserNotFoundException;

/**
 * A UserAdministration Interface contains methods to create, edit and remove:
 * <ul>
 * <li>the User,</li>
 * <li>the Organization.</li>
 * </ul>
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: UserManagementService.java,v 1.5 2007-11-14 10:32:37 jamess Exp
 *          $
 * 
 */
public interface UserManagementService {

	/**
	 * Creates a new User.
	 * 
	 * @param user
	 *            the User-Object, that contains all user-specific informationen
	 *            for the new user.
	 * @return the unique identifier for the new user Object.
	 * @throws InconsistentUserConstraintException
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public UUID createUser(User user)
			throws InconsistentUserConstraintException;

	/**
	 * Edit and Store the user-specific informations.
	 * 
	 * @param uuid
	 *            the inique identifier for the user-object, that should be
	 *            changed.
	 * @param user
	 *            User-Object, that contains the new informationen for this
	 *            user.
	 * @throws UserNotFoundException
	 * @throws InconsistentUserConstraintException
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean updateUser(UUID id, User user) throws UserNotFoundException,
			InconsistentUserConstraintException;

	/**
	 * Removes the User-Object from the system. The User-Object could be
	 * deleted, if (and only if) the List of his contexts in the system is
	 * empty.
	 * 
	 * 
	 * @param uuid
	 *            the unique identifier for the user, that should be deleted.
	 * @return true, if the delete operation was successful, false, else.
	 * @throws UserNotFoundException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean removeUser(UUID uuid) throws UserNotFoundException;

	/**
	 * Creates a new Organization.
	 * 
	 * @param organization
	 *            the Organization-Object, that contains all
	 *            organization-specific informationen for the new organization.
	 * @return the unique identifier for the new organization Object.
	 * @throws InconsistentOrganizationConstraintException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = InconsistentOrganizationConstraintException.class)
	public UUID createOrganization(Organization organization)
			throws InconsistentOrganizationConstraintException;

	/**
	 * Edit and Store the organization-specific informations.
	 * 
	 * @param uuid
	 *            the inique identifier for the organization-object, that should
	 *            be changed.
	 * @param organization
	 *            Organization-Object, that contains the new informationen for
	 *            this organization.
	 * @throws OrganizationNotFoundException
	 * @throws InconsistentOrganizationConstraintException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean updateOrganization(UUID id, Organization organization)
			throws OrganizationNotFoundException,
			InconsistentOrganizationConstraintException;

	/**
	 * Removes the Organization-Object from the system. An Organization could be
	 * deleted from the system, if(and only if) its user list is empty and there
	 * are no other references to this organization in the system.
	 * 
	 * 
	 * @param uuid
	 *            the unique identifier for the organization, that should be
	 *            deleted.
	 * @return true, if the delete operation was successful, false, else.
	 * @throws OrganizationNotFoundException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean removeOrganization(UUID uuid)
			throws OrganizationNotFoundException;

	/**
	 * This methods should be used by ContextClient to simplify
	 * UpdateUser-operation.
	 * 
	 * @param uuid
	 *            unique identifier of the user.
	 * @return user data.
	 * @throws UserNotFoundException
	 */
	@Transactional(readOnly = true)
	public User getUserData(UUID uuid) throws UserNotFoundException;

	/**
	 * This method should be used by ContextClient to simplify
	 * UpdateOrganization-operation.
	 * 
	 * @param uuid
	 *            unique identifier of the organization.
	 * @return organization data.
	 * @throws OrganizationNotFoundException
	 */
	@Transactional(readOnly = true)
	public Organization getOrganization(UUID uuid)
			throws OrganizationNotFoundException;

}
