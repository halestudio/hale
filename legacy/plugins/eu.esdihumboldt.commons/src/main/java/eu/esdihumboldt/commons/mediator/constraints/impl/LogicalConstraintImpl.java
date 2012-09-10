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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.LanguageConstraint;
import eu.esdihumboldt.specification.mediator.constraints.LogicalConstraint;
import eu.esdihumboldt.specification.util.IdentifierManager;

/**
 * This is the default implementation of the LogicalConstraint. It is
 * {@link Serializable}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: LogicalConstraintImpl.java,v 1.3 2007-11-16 15:08:27 pitaeva
 *          Exp $
 */
public class LogicalConstraintImpl implements LogicalConstraint, Serializable {

	// Fields ..................................................................
	private UUID identifier;
	/**
     *
     */
	private static final long serialVersionUID = 1L;
	/** The unique constraint identifire in the database */
	private long id;
	/**
	 * The unique identifier in the current VM.
	 */
	private final long uid;
	/**
	 * The List storing the list of Constraint objects defining the logical
	 * constraint.
	 */
	private final List<Constraint> constraints;
	/**
	 * The LogicalOperator that binds the constraints.
	 */
	private final LogicalOperator operator;
	/**
	 * the {@link ConstraintSource} of this {@link LanguageConstraint}.
	 */
	private final ConstraintSource constraintSource;
	private boolean write = false;
	private boolean status;

	private boolean sharedConstraint = false;

	// Constructors ............................................................
	/**
	 * default no-args constructor
	 */
	protected LogicalConstraintImpl() {
		this.uid = IdentifierManager.next();
		this.constraints = new ArrayList<Constraint>();
		this.constraintSource = ConstraintSource.parameter;
		// sets default operator to EQUAls
		this.operator = LogicalOperator.EQUALS;
		this.status = false;

	}

	/**
	 * @param _constraints
	 *            The List of Constraints to group together. The Constraints may
	 *            be of any type implementing Constraint.
	 * @param _operator
	 *            The {@link LogicalOperator} that is used to bind the
	 *            Constraints.
	 */
	public LogicalConstraintImpl(List<Constraint> _constraints,
			LogicalOperator _operator) {
		this.uid = IdentifierManager.next();
		this.constraints = _constraints;
		this.operator = _operator;
		this.constraintSource = ConstraintSource.parameter;

	}

	/**
	 * Simple constructor for the binding of two Constraints. Doesn't require a
	 * list.
	 * 
	 * @param _a
	 *            the first {@link Constraint}
	 * @param _b
	 *            the second {@link Constraint}
	 * @param _operator
	 *            the {@link LogicalOperator} that is used to bind the
	 *            Constraints.
	 */
	public LogicalConstraintImpl(Constraint _a, Constraint _b,
			LogicalOperator _operator) {
		this.uid = IdentifierManager.next();
		this.constraints = new ArrayList<Constraint>();
		this.constraints.add(_a);
		this.constraints.add(_b);
		this.operator = _operator;
		this.constraintSource = ConstraintSource.parameter;
		this.status = false;
	}

	// Operations implemented from LogicalConstraint ...........................
	/**
	 * This operation returns a copy of the list of Constraints bound to this
	 * logical constraint.
	 * 
	 * @see eu.esdihumboldt.specification.mediator.constraints.LogicalConstraint#getBoundConstraints()
	 */
	public List<Constraint> getBoundConstraints() {
		return constraints;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.LogicalConstraint#getLogicalOperator()
	 */
	public LogicalOperator getLogicalOperator() {
		return this.operator;
	}

	// Operations implemented from Constraint ..................................
	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#isSatisfied()
	 */
	public boolean isSatisfied() {
		// evaluate isSatisfied methods of bound Constraints beforehand. Reduces
		// code, but misses shortcircuiting optimization opportunity.
		int true_counter = 0;
		for (Constraint constraint : this.constraints) {
			if (constraint.isSatisfied()) {
				true_counter++;
			}
		}

		// all bound constraints have to be true.
		if (this.operator == LogicalOperator.AND) {
			if (true_counter == this.constraints.size()) {
				return this.status = true;
			}
		}

		// it suffices if one bound constraint was true.
		if (this.operator == LogicalOperator.OR) {
			if (true_counter >= 1) {
				return this.status = true;
			}
		}

		// exactly one constraint has to be true.
		if (this.operator == LogicalOperator.XOR) {
			if (true_counter == 1) {
				return this.status = true;
			}
		}

		// either all constraints are true or false.
		if (this.operator == LogicalOperator.EQUALS) {
			if ((true_counter == this.constraints.size())
					|| (true_counter == 0)) {
				return this.status = true;
			}
		}
		return this.status;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#getConstraintSource()
	 */
	public ConstraintSource getConstraintSource() {
		return this.constraintSource;
	}

	// Other operations ........................................................
	/**
	 * @return the Uid that has been assigned to this LanguageConstraint.
	 */
	public long getUid() {
		return this.uid;
	}

	/**
	 * @return unique identifier for the database.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.id = id;
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
		if (constraint != null || (constraint instanceof LogicalConstraint)) {

			LogicalConstraint logicalConstraint = (LogicalConstraint) constraint;

			if (this.getBoundConstraints().equals(
					logicalConstraint.getBoundConstraints())
					&& this.getLogicalOperator().equals(
							logicalConstraint.getLogicalOperator())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public void setSatisfied(boolean _satisfied) {
		this.status = _satisfied;
	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}
}
