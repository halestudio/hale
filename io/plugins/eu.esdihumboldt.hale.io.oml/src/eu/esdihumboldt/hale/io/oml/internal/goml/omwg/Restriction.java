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

package eu.esdihumboldt.hale.io.oml.internal.goml.omwg;

import java.math.BigInteger;
import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext.ValueClass;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueExpression;

/**
 * A {@link Restriction} is used to define a condition for a
 * {@link FeatureClass}. It represents the omwg:RestrictionType.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public class Restriction {

	/**
	 * <xs:element ref="omwg:onAttribute"/> TODO in future: onAttribute can also
	 * refer to a Relation between (Feature)Classes
	 */
	// FIXME clear with MdV
	// private Property onAttribute;

	/**
	 * TODO explain. <xs:element ref="omwg:comparator"/>
	 */
	private ComparatorType comparator;

	/**
	 * TODO explain. <xs:element name="value" type="omwg:valueExprType"
	 * maxOccurs="unbounded" />
	 */
	private List<IValueExpression> value;

	/**
	 * if List of value expressions is empty use the value class
	 */
	private ValueClass valueClass;

	/**
	 * The cql String can be used as an alternative to using the fields above.
	 * 
	 * <xs:element ref="goml:cqlStr" minOccurs="0" maxOccurs="1" />
	 */
	private String cqlStr;

	/**
	 * sequnce field describing the position of the value condition in the list
	 */

	private BigInteger seq;

	// constructors ............................................................

	public BigInteger getSeq() {
		return seq;
	}

	public void setSeq(BigInteger seq) {
		this.seq = seq;
	}

	/**
	 * 
	 * @param value
	 * 
	 */

	public Restriction(List<IValueExpression> value) {
		super();
		this.value = value;
	}

	// getters / setters .......................................................

	/**
	 * @return the comparator
	 */
	public ComparatorType getComparator() {
		return comparator;
	}

	/**
	 * @param comparator the comparator to set
	 */
	public void setComparator(ComparatorType comparator) {
		this.comparator = comparator;
	}

	/**
	 * @return the value
	 */
	public List<IValueExpression> getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(List<IValueExpression> value) {
		this.value = value;
	}

	/**
	 * @return the cqlStr
	 */
	public String getCqlStr() {
		return cqlStr;
	}

	/**
	 * @param cqlStr the cqlStr to set
	 */
	public void setCqlStr(String cqlStr) {
		this.cqlStr = cqlStr;
	}

	/**
	 * 
	 * @return ValueClass
	 */
	public ValueClass getValueClass() {
		return valueClass;
	}

	/**
	 * 
	 * @param valueClass
	 */
	public void setValueClass(ValueClass valueClass) {
		this.valueClass = valueClass;
	}

	@Override
	public String toString() {
		return "Restriction [comparator=" + comparator + ", cqlStr=" + cqlStr + ", seq=" + seq
				+ ", value=" + value + ", valueClass=" + valueClass + "]";
	}

}
