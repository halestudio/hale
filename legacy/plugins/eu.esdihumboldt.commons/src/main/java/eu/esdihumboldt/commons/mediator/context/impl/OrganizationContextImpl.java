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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint;
import eu.esdihumboldt.specification.mediator.context.Context;
import eu.esdihumboldt.specification.mediator.context.DefaultContext;
import eu.esdihumboldt.specification.mediator.context.OrganizationContext;
import eu.esdihumboldt.specification.mediator.usermanagement.Organization;
import eu.esdihumboldt.specification.util.ConstraintTypeKey;

/**
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: OrganizationContextImpl.java,v 1.10 2007-12-18 13:26:16 pitaeva
 *          Exp $
 */
public class OrganizationContextImpl implements OrganizationContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Object variables
	private long id;
	private int priority;
	private Organization organization;
	private DefaultContext defaultContext;
	private MetadataConstraint organizationConstraint;
	private UUID uuid;
	private String title;

	/**
	 * no-args constructor
	 */

	public OrganizationContextImpl() {
	}

	/**
	 * 
	 * @param _organization
	 * @param _defaultContext
	 * @param _languageCons
	 * @param _metadataconstraint
	 * @param _qualityconstraint
	 * @param _resoCons
	 * @param _thematicCons
	 * @param _temporalCons
	 * @param _spatialCons
	 * @param _portrayalCons
	 * @param _organizationConstraint
	 */
	public OrganizationContextImpl(Organization _organization,
			DefaultContext _defaultContext,
			MetadataConstraint _organizationConstraint) {

		this.organization = _organization;
		this.defaultContext = _defaultContext;
		this.organizationConstraint = _organizationConstraint;
		this.uuid = UUID.randomUUID();
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.OrganizationContext#getOrganization()
	 */
	public Organization getOrganization() {
		return this.organization;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.OrganizationContext#getParentContext()
	 */
	public DefaultContext getParentContext() {
		return this.defaultContext;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.Context#getAllConstraints()
	 */
	public Map<ContextType, Set<Constraint>> getAllConstraints() {
		// A mapping of all constraints for a given context type
		Map<ContextType, Set<Constraint>> result = new HashMap<ContextType, Set<Constraint>>();

		// Mapping the constraints to organization context type
		result.put(this.getContextType(), this.assembleConstraintSet());

		// Mapping the constraints to default context type
		DefaultContext def_context = this.getParentContext();
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
					+ ") presented to Organization Context!");
		}

	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.Context#getCombinedConstraints(eu.esdihumboldt.specification.mediator.context.Context.ContextType)
	 */
	public Set<Constraint> getCombinedConstraints(ContextType type) {
		Map<ConstraintTypeKey, Constraint> combinedconstraints = new HashMap<ConstraintTypeKey, Constraint>();

		// get all the Constraints for the DefaultContext and write them to the
		// result Set.
		Map<ContextType, Set<Constraint>> all_constraints = this
				.getAllConstraints();

		Set<Constraint> def_context_constraints = all_constraints
				.get(Context.ContextType.Default);
		for (Constraint this_constraint : def_context_constraints) {
			combinedconstraints.put(new ConstraintTypeKey(this_constraint),
					this_constraint);
		}

		// get all the Constraints for the OrganisationContext and write them to
		// the result Set.
		Set<Constraint> org_context_constraints = all_constraints
				.get(Context.ContextType.Organisation);
		for (Constraint this_constraint : org_context_constraints) {
			combinedconstraints.put(new ConstraintTypeKey(this_constraint),
					this_constraint);
		}

		return new HashSet<Constraint>(combinedconstraints.values());

	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.Context#getContextType()
	 */
	public ContextType getContextType() {
		return Context.ContextType.Organisation;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.context.OrganizationContext#getOrganizationConstraint()
	 */
	public MetadataConstraint getOrganizationConstraint() {
		return this.organizationConstraint;
	}

	protected Set<Constraint> assembleConstraintSet() {
		Set<Constraint> result = ((DefaultContextImpl) defaultContext)
				.assembleConstraintSet();
		result.add(this.organizationConstraint);
		return result;
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

	/**
	 * @param defaultContext
	 *            the defaultContext to set
	 */
	public void setDefaultContext(DefaultContext defaultContext) {
		this.defaultContext = defaultContext;
	}

	/**
	 * @return the defaultContext
	 */
	public DefaultContext getDefaultContext() {
		return defaultContext;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return priority for this context in the organization context list.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            priority for this context in the organization context list.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/**
	 * @param organizationConstraint
	 *            the organizationConstraint to set
	 */
	public void setOrganizationConstraint(
			MetadataConstraint organizationConstraint) {
		this.organizationConstraint = organizationConstraint;
	}

	public UUID getContextID() {

		return this.uuid;
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
}
