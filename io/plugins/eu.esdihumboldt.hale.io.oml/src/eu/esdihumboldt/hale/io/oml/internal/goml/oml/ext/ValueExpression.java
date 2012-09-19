/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext;

import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueExpression;

/**
 * A {@link ValueExpression} is used to deal with literal values and ranges. It
 * represents the goml:ValueExprType. FIXME not clear yet how to deal with other
 * kinds of 'value'
 * 
 * @author Marian de Vries
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
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
	@Override
	public String getLiteral() {
		return literal;
	}

	/**
	 * @param literal the literal to set
	 */
	public void setLiteral(String literal) {
		this.literal = literal;
	}

	/**
	 * @return the min
	 */
	@Override
	public String getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(String min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	@Override
	public String getMax() {
		return max;
	}

	/**
	 * @param max the max to set
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
	 * @param apply the apply to set
	 */
	public void setApply(Function apply) {
		this.apply = apply;
	}

	@Override
	public String toString() {
		return "ValueExpression [apply=" + apply + ", literal=" + literal + ", max=" + max
				+ ", min=" + min + "]";
	}

}
