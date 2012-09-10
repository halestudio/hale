/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.mediator.constraints.impl;

import java.io.Serializable;
import java.util.UUID;

import eu.esdihumboldt.specification.annotations.concurrency.Immutable;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.LanguageConstraint;
import eu.esdihumboldt.specification.mediator.constraints.ServiceConstraint;
import eu.esdihumboldt.specification.util.IdentifierManager;

/**
 * Default implementation of a ServiceConstraint which is {@link Serializable}
 * and {@link Immutable}.
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version Framework v1.0
 */
public class ServiceConstraintImpl implements ServiceConstraint, Serializable {

	// Fields ..................................................................
	private UUID identifier;

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	/**
	 * The unique constraint identifier in the datatabase.
	 */
	private long id;

	/**
	 * The unique identifier in the current VM.
	 */
	private long uid;

	/**
	 * Alphanumeric value identifying the OGC service (e.g."WMS").
	 */
	private String serviceType;

	/**
	 * Alphanumeric value identifying the OGC service version (e.g."1.1.0").
	 */
	private String serviceVersion;

	/**
	 * the {@link ConstraintSource} of this {@link ServiceConstraint}.
	 */
	private ConstraintSource constraintSource;

	/**
	 * The status of this constraint.
	 */
	private boolean satisfied = false;

	private boolean write = false;

	private boolean sharedConstraint = false;

	// Constructors
	// ..............................................................
	/**
	 * protected default constructor for the hibernate and castor needs only
	 * must be public, because of Castor-requirements.
	 */
	public ServiceConstraintImpl() {
		// this.serviceVersion = null;
		// this.serviceType = null;
		// this.constraintSource = ConstraintSource.parameter;
		// this.uid = IdentifierManager.next();
		// this.satisfied = true;
	}

	/**
	 * Use this Constructor to specify a single Service to be used.
	 * 
	 * @param _serviceType
	 *            serviceType to use.
	 * @param _serviceVersion
	 *            serviceVersion to use.
	 */
	public ServiceConstraintImpl(String _serviceType, String _serviceVersion) {
		this.serviceType = _serviceType;
		this.serviceVersion = _serviceVersion;
		this.constraintSource = ConstraintSource.parameter;
		this.uid = IdentifierManager.next();
		this.satisfied = false;
	}

	/**
	 * Use this Constructor to specify a single Service as well as a specific
	 * {@link ConstraintSource}.
	 * 
	 * @param _serviceType
	 *            serviceType to use.
	 * @param _serviceVersion
	 *            serviceVersion to use.
	 * @param _constraintSource
	 *            the {@link ConstraintSource} that this
	 *            {@link ServiceConstraint} originates from.
	 */
	public ServiceConstraintImpl(String _serviceType, String _serviceVersion,
			ConstraintSource _constraintSource) {
		this.serviceType = _serviceType;
		this.serviceVersion = _serviceVersion;
		this.constraintSource = _constraintSource;
		this.uid = IdentifierManager.next();
		this.satisfied = false;
	}

	// Operations implemented from ServiceConstraint
	// .............................
	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.ServiceConstraint#getServiceType()
	 */
	public String getServiceType() {
		return this.serviceType;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.ServiceConstraint#getServiceVersion()
	 */
	public String getServiceVersion() {
		return this.serviceVersion;
	}

	// Operations implemented from Constraint
	// .....................................
	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#getConstraintSource()
	 */
	public ConstraintSource getConstraintSource() {
		return this.constraintSource;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#getId()
	 */
	public long getId() {
		return id;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#isSatisfied()
	 */
	public boolean isSatisfied() {
		return this.satisfied;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}

	// Other operations
	// ...........................................................
	/**
	 * @return the UUID uniquely identifying this {@link LanguageConstraint}.
	 */
	public long getUid() {
		return this.uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(long uid) {
		this.uid = uid;
	}

	/**
	 * @param constraintSource
	 *            the constraintSource to set.
	 */
	public void setConstraintSource(ConstraintSource constraintSource) {
		this.constraintSource = constraintSource;
	}

	/**
	 * @param satisfied
	 *            the satisfied to set.
	 */
	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}

	/**
	 * @param serviceType
	 *            the service type to set.
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @param serviceVersion
	 *            the service version to set.
	 */
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public UUID getIdentifier() {
		return identifier;
	}

	public void setIdentifier(UUID identifier) {
		this.identifier = identifier;
	}

	public boolean isFinalized() {
		return this.write;
	}

	public void setFinalized(boolean write) {
		this.write = write;
	}

	public boolean compatible(Constraint constraint) {
		if (constraint != null || (constraint instanceof ServiceConstraint)) {

			ServiceConstraint serviceConstraint = (ServiceConstraint) constraint;
			boolean isSameType = this.serviceType.equals(serviceConstraint
					.getServiceType());
			boolean isSameVersion = this.serviceVersion
					.equals(serviceConstraint.getServiceVersion());
			if (isSameVersion && isSameType) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}

}
