/**
 * 
 */
package eu.esdihumboldt.commons.mediator.constraints.impl;

import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.IdentifierConstraint;

/**
 * @author Bernd Schneiders, Logica
 * 
 */
public class IdentifierConstraintImpl implements IdentifierConstraint {

	Set<String> featureIDs;

	private UUID identifier;

	/**
	 * The status of this constraint.
	 */
	private boolean satisfied;

	private boolean write = false;

	private boolean sharedConstraint = false;

	public UUID getIdentifier() {
		return identifier;
	}

	public void setIdentifier(UUID identifier) {
		this.identifier = identifier;
	}

	public IdentifierConstraintImpl(Set<String> featureIDs) {
		this.featureIDs = featureIDs;
		this.satisfied = false;
	}

	/**
	 * @param featureIDs
	 *            the featureNames to set
	 */
	public void setFeatureIDs(Set<String> featureIDs) {
		this.featureIDs = featureIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.mediator.constraints.FeatureConstraint#getFeatureNames()
	 */
	public Set<String> getFeatureIDs() {
		return this.featureIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.mediator.constraints.Constraint#getConstraintSource()
	 */
	public ConstraintSource getConstraintSource() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.esdihumboldt.mediator.constraints.Constraint#getId()
	 */
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.esdihumboldt.mediator.constraints.Constraint#isSatisfied()
	 */
	public boolean isSatisfied() {
		// TODO Auto-generated method stub
		return this.satisfied;
	}

	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.esdihumboldt.mediator.constraints.Constraint#setId(long)
	 */
	public void setId(long arg0) {
		// TODO Auto-generated method stub
	}

	public boolean isFinalized() {
		return this.write;
	}

	public void setFinalized(boolean write) {
		this.write = write;
	}

	public boolean compatible(Constraint constraint) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}

}
