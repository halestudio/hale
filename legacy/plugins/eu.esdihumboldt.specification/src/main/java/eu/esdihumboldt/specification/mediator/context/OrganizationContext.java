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
import eu.esdihumboldt.specification.mediator.usermanagement.Organization;

/**
 * An OrganizationContext Interface is a SubInterface of the DefaultContext and
 * allows additionaly acceess to the Internal Organization Information.
 * 
 * 
 * 
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: OrganizationContext.java,v 1.3 2007-12-18 13:26:16 pitaeva Exp
 *          $
 * 
 */
public interface OrganizationContext extends Context {

	/**
	 * @return the OrganizationConstraint, which describes interanal/adress
	 *         Structure of the organization.
	 * 
	 */
	public MetadataConstraint getOrganizationConstraint();

	/**
	 * 
	 * 
	 * @return the Organization this context bolongs to.
	 */
	public Organization getOrganization();

	public void setOrganization(Organization organization);

	public void setOrganizationConstraint(
			MetadataConstraint organizationConstraint);

	public DefaultContext getDefaultContext();

	public void setDefaultContext(DefaultContext defaultContext);

	/**
	 * Allows access to the ParentContext, that is a mandatory element for each
	 * OrganizationS context. It contains all default constraints for the
	 * ChildContext.
	 * 
	 * @return DefaultContext parentContext.
	 */

	public DefaultContext getParentContext();

}
