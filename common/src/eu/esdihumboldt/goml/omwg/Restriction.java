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

package eu.esdihumboldt.goml.omwg;

import java.util.List;

import eu.esdihumboldt.goml.oml.ext.ValueClass;
import eu.esdihumboldt.goml.oml.ext.ValueExpression;

/**
 * A {@link Restriction} is used to define a condition for a
 * {@link FeatureClass}. It represents the omwg:RestrictionType.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class Restriction {

	/**
	 * <xs:element ref="omwg:onAttribute"/> TODO in future: onAttribute can also
	 * refer to a Relation between (Feature)Classes
	 */
	private Property onAttribute;

	/**
	 * TODO explain. 
	 * <xs:element ref="omwg:comparator"/>
	 */
	private ComparatorType comparator;

	/**
	 * TODO explain. 
	 * <xs:element name="value" type="omwg:valueExprType" maxOccurs="unbounded" />
	 */
	private List<ValueExpression> value;
	
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

	// constructors ............................................................

	/**
	 * TODO explain.
	 * 
	 * @param onAttribute
	 * @param value
	 */
	public Restriction(Property onAttribute, List<ValueExpression> value) {
		super();
		this.onAttribute = onAttribute;
		this.value = value;
	}

	// getters / setters .......................................................

	/**
	 * @return the onAttribute
	 */
	public Property getOnAttribute() {
		return onAttribute;
	}

	/**
	 * @param onAttribute
	 *            the onAttribute to set
	 */
	public void setOnAttribute(Property onAttribute) {
		this.onAttribute = onAttribute;
	}

	/**
	 * @return the comparator
	 */
	public ComparatorType getComparator() {
		return comparator;
	}

	/**
	 * @param comparator
	 *            the comparator to set
	 */
	public void setComparator(ComparatorType comparator) {
		this.comparator = comparator;
	}

	/**
	 * @return the value
	 */
	public List<ValueExpression> getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(List<ValueExpression> value) {
		this.value = value;
	}

	/**
	 * @return the cqlStr
	 */
	public String getCqlStr() {
		return cqlStr;
	}

	/**
	 * @param cqlStr
	 *            the cqlStr to set
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

}
