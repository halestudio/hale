/**
 * 
 */
package eu.esdihumboldt.specification.mediator.constraints;

import org.opengis.filter.expression.Expression;

/**
 * @author Bernd Schneiders, Logica
 * 
 */
public interface AttributeConstraint extends Constraint {

	public String getOperatorName();

	public String getPropertyName();

	public Expression getExpression1();

	public Expression getExpression2();
}
