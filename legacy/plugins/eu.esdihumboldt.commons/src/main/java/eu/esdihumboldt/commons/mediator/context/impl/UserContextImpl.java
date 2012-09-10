/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.mediator.context.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint;
import eu.esdihumboldt.specification.mediator.context.Context;
import eu.esdihumboldt.specification.mediator.context.DefaultContext;
import eu.esdihumboldt.specification.mediator.context.OrganizationContext;
import eu.esdihumboldt.specification.mediator.context.UserContext;
import eu.esdihumboldt.specification.mediator.usermanagement.Organization;
import eu.esdihumboldt.specification.mediator.usermanagement.User;

/**
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: UserContextImpl.java,v 1.9 2007-12-18 13:26:16 pitaeva Exp $
 */
public class UserContextImpl implements UserContext {

	// Attributes ..............................................................

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OrganizationContext organizationContext;
	private User user;
	private long id;
	private int priority;
	private UUID uuid;
	private String title;
	private MetadataConstraint userConstraint;

	// Constructors ............................................................

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * no-args constructor
	 */

	public UserContextImpl() {
	}

	/**
	 * @param _organizationcontext
	 * @param _user
	 * @param _languageCons
	 * @param _metadataconstraint
	 * @param _qualityconstraint
	 * @param _resoCons
	 * @param _thematicCons
	 * @param _temporalCons
	 * @param _spatialCons
	 * @param _portrayalCons
	 */
	public UserContextImpl(OrganizationContext _organizationcontext, User _user) {

		this.organizationContext = _organizationcontext;
		this.user = _user;
		this.uuid = UUID.randomUUID();
	}

	// UserContext operations ..................................................

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.UserContext#getParentContext()
	 */
	public OrganizationContext getParentContext() {
		return this.organizationContext;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.UserContext#getUser()
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.Context#getAllConstraints()
	 */
	public Map<ContextType, Set<Constraint>> getAllConstraints() {
		Map<ContextType, Set<Constraint>> result = new HashMap<ContextType, Set<Constraint>>();
		result.put(this.getContextType(), this.assembleConstraintSet());

		OrganizationContext org_context = this.getParentContext();
		result.put(org_context.getContextType(),
				org_context.getAllConstraints(org_context.getContextType()));

		DefaultContext def_context = org_context.getParentContext();
		result.put(def_context.getContextType(),
				def_context.getAllConstraints(def_context.getContextType()));
		return result;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.Context#getAllConstraints(eu.esdihumboldt.specification.mediator.context.Context.ContextType)
	 */
	public Set<Constraint> getAllConstraints(ContextType _type) {
		if (_type == this.getContextType()) {
			return this.assembleConstraintSet();
		} else {
			throw new RuntimeException("Wrong ContextType (" + _type
					+ ") presented to UserContext!");
		}
	}

	/**
	 * This operation implements getCombinedConstraints as defined in the
	 * interface, but will only work reliable as long as one implementation of
	 * each {@link Constraint} is in use.
	 * 
	 * @see eu.esdihumboldt.specification.mediator.context.Context#getCombinedConstraints(eu.esdihumboldt.specification.mediator.context.Context.ContextType)
	 */
	public Set<Constraint> getCombinedConstraints(ContextType type) {
		// FIXME Thorsten Reitz This implementation is not using the CTC yet.

		Set<Constraint> result = ((DefaultContextImpl) this.organizationContext
				.getParentContext()).assembleConstraintSet();

		// get all the Constraints for the UserContext and write them to the
		// result Set.
		Set<Constraint> usr_context_constraints = this.assembleConstraintSet();
		for (Constraint this_constraint : usr_context_constraints) {
			result.add(this_constraint);
		}

		return result;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.Context#getContextType()
	 */
	public ContextType getContextType() {
		return Context.ContextType.User;
	}

	/**
	 * @return a Set with all Constraints defined for this UserContext.
	 */
	protected Set<Constraint> assembleConstraintSet() {
		Set<Constraint> result = ((DefaultContextImpl) this.organizationContext
				.getParentContext()).assembleConstraintSet();
		result.add(this.getUserConstraint());
		return result;
	}

	public MetadataConstraint getUserConstraint() {
		return this.userConstraint;
	}

	public void setUserConstraint(MetadataConstraint userConstraint) {
		this.userConstraint = userConstraint;
	}

	/**
	 * Set the user
	 * 
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the organizationContext
	 */
	public OrganizationContext getOrganizationContext() {
		return organizationContext;
	}

	/**
	 * @param organizationContext
	 *            the organizationContext to set
	 */
	public void setOrganizationContext(OrganizationContext organizationContext) {
		this.organizationContext = organizationContext;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getContextID() {
		return this.uuid;
	}

	public Organization getOrganization() {
		return this.organizationContext.getOrganization();
	}

	// public void setOrganization(Organization organization)
	// {
	// // this.organizationContext.setOrganization( organization );
	// }
	//
	// public void setOrganizationConstraint(MetadataConstraint
	// organizationConstraint) {
	// //
	// this.organizationContext.setOrganizationConstraint(organizationConstraint);
	// }
	//
	// public MetadataConstraint getOrganizationConstraint() {
	// return this.organizationContext.getOrganizationConstraint();
	// }
	//
	// public void setDefaultContext(DefaultContext defaultContext)
	// {
	// // this.organizationContext.setDefaultContext( defaultContext);
	// }
	//
	// public DefaultContext getDefaultContext()
	// {
	// return this.organizationContext.getDefaultContext();
	// }

}
