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
package eu.esdihumboldt.specification.mediator;

import java.util.UUID;

import eu.esdihumboldt.specification.mediator.context.Context;
import eu.esdihumboldt.specification.mediator.context.exceptions.DefaultContextNotFoundException;
import eu.esdihumboldt.specification.mediator.context.exceptions.InconsistentContextConstraintException;
import eu.esdihumboldt.specification.mediator.context.exceptions.OrganizationContextNotFoundException;
import eu.esdihumboldt.specification.mediator.context.exceptions.UserContextNotFoundException;

/**
 * A ContextService implements the methods to
 * <ul>
 * <li>allow the Interface Controller access to the Context Inforamation,</li>
 * <li>manage each User's Context, i.e. his configuration, preferred languages,
 * role to be used by mediator nodes.</li>
 * </ul>
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: ContextService.java,v 1.3 2007-11-15 10:26:36 pitaeva Exp $
 */

public interface ContextService {

	/**
	 * It allows the Interface Controller access to the Context Information. The
	 * Context Object consists in this case of the following three components
	 * <ul>
	 * <li>Default Context,</li>
	 * <li>Organization Context,</li>
	 * <li>User Context.</li>
	 * </ul>
	 * 
	 * @param contextID
	 *            unique context identifier.
	 * @return Context context.
	 */

	// @Transactional(readOnly=true)
	public Context getContext(UUID contextID);

	/**
	 * It allows the Interface Controller access to the organization part of the
	 * Context Information.
	 * 
	 * 
	 * @param contextID
	 *            unique context identifier.
	 * @return Context context, organization context.
	 */

	// @Transactional(readOnly=true)
	public Context getOrganizationContext(UUID contextID)
			throws OrganizationContextNotFoundException;

	/**
	 * It allows the Interface Controller access to the default part of the
	 * Context Information.
	 * 
	 * 
	 * @param contextID
	 *            unique context identifier.
	 * @return Context context, default context.
	 */

	// @Transactional(readOnly=true)
	public Context getDefaultContext(UUID contextID)
			throws DefaultContextNotFoundException;

	/**
	 * It allows the Interface Controller access to the user part of the Context
	 * Information.
	 * 
	 * 
	 * @param contextID
	 *            unique context identifier.
	 * @return Context context, user context.
	 */

	// @Transactional(readOnly=true)
	public Context getUserContext(UUID contextID)
			throws UserContextNotFoundException;

	/**
	 * 
	 * It allows the Interface Controller access to the WebMapContext part of
	 * the Context Information.
	 * 
	 * 
	 * @param ID
	 *            unique context identifier.
	 * @return Context context, WebMapContext.
	 */

	// @Transactional(readOnly=true)
	public String getWebMapContext(UUID contextID);

	// UserManagement methods

	/**
	 * It allows to the user with administrator's permission create a new
	 * context profile.
	 * 
	 * @param context
	 *            the context object to add to the Service.
	 * @return the UUID that was assigned to the newly created Context.
	 * @throws InconsistentContextConstraintException
	 */
	// @Transactional(propagation=Propagation.REQUIRES_NEW)
	public UUID putContext(Context context)
			throws InconsistentContextConstraintException;

	/**
	 * It allows to the user with administrator's permission remove an existing
	 * context profile.
	 * 
	 * @param contextID
	 *            unique context identifier.
	 * @return true, if delete operation was successful false, else.
	 */

	// @Transactional(propagation=Propagation.REQUIRED)
	public boolean removeContext(UUID contextID);

	/**
	 * It allows to the user with the administrator's permission update an
	 * existing context profile.
	 * 
	 * @param contextID
	 *            unique context identifier.
	 * 
	 * @param context
	 *            the new context inforamtion.
	 * 
	 * @return true, if update operation was successful false, else.
	 * @throws InconsistentContextConstraintException
	 */

	// @Transactional(propagation=Propagation.REQUIRED)
	public boolean updateContext(UUID contextID, Context context)
			throws InconsistentContextConstraintException;

}
