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

package eu.esdihumboldt.commons.goml.oml.ext;

import eu.esdihumboldt.specification.cst.align.ext.IValueExpression;

/**
 * A {@link ValueExpression} is used to deal with literal values and ranges. It
 * represents the goml:ValueExprType. FIXME not clear yet how to deal with other
 * kinds of 'value'
 * 
 * @author Marian de Vries
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class ValueExpression implements IValueExpression {

	private String literal;
	private String min;
	private String max;
	private Function apply;

	// constructors ............................................................

	/**
	 * @param literal
	 */
	public ValueExpression(String literal) {
		super();
		this.literal = literal;
	}

	// getters / setters .......................................................

	/**
	 * @return the literal value represented by this {@link ValueExpression}.
	 */
	public String getLiteral() {
		return literal;
	}

	/**
	 * @param literal
	 *            the literal to set
	 */
	public void setLiteral(String literal) {
		this.literal = literal;
	}

	/**
	 * @return the min
	 */
	public String getMin() {
		return min;
	}

	/**
	 * @param min
	 *            the min to set
	 */
	public void setMin(String min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public String getMax() {
		return max;
	}

	/**
	 * @param max
	 *            the max to set
	 */
	public void setMax(String max) {
		this.max = max;
	}

	/**
	 * @return the apply
	 */
	public Function getApply() {
		return apply;
	}

	/**
	 * @param apply
	 *            the apply to set
	 */
	public void setApply(Function apply) {
		this.apply = apply;
	}

	@Override
	public String toString() {
		return "ValueExpression [apply=" + apply + ", literal=" + literal
				+ ", max=" + max + ", min=" + min + "]";
	}

}
