package eu.esdihumboldt.commons.mediator.constraints.impl;

import java.util.UUID;

import org.opengis.filter.expression.Expression;

import eu.esdihumboldt.specification.mediator.constraints.AttributeConstraint;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;

public class AttributeConstraintImpl implements AttributeConstraint {

	private String propertyName;

	private String operatorName;

	private Expression expression1;

	private Expression expression2;

	private UUID identifier;

	private boolean write = false;

	/**
	 * The status of this constraint.
	 */
	private boolean satisfied = false;

	private boolean sharedConstraint = false;

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

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName
	 *            the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return the operatorName
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * @param operatorName
	 *            the operatorName to set
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * @return the expression1
	 */
	public Expression getExpression1() {
		return expression1;
	}

	/**
	 * @param expression1
	 *            the expression1 to set
	 */
	public void setExpression1(Expression expression1) {
		this.expression1 = expression1;
	}

	/**
	 * @return the expression2
	 */
	public Expression getExpression2() {
		return expression2;
	}

	/**
	 * @param expression2
	 *            the expression2 to set
	 */
	public void setExpression2(Expression expression2) {
		this.expression2 = expression2;
	}

	public AttributeConstraintImpl(String propertyName, String operatorName,
			Expression expression1, Expression expression2) {
		super();
		this.propertyName = propertyName;
		this.operatorName = operatorName;
		this.expression1 = expression1;
		this.expression2 = expression2;
		this.satisfied = false;
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
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}
}