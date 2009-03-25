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

/**
 * A {@link Restriction} is used to define a Condition. It represents the 
 * omwg:RestrictionType.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Restriction {
	
	/**
	 * <xs:element ref="omwg:onAttribute"/>
	 */
	private Property onAttribute;
	
	/**
	 * <xs:element ref="omwg:comparator"/>
	 */
	private Property comparator;
	
	/**
	 * <xs:element name="value" type="omwg:valueExprType" maxOccurs="unbounded" />
	 */
	private List<ValueExprType> values;
	
	/**
	 * <xs:element ref="goml:cqlStr" minOccurs="0" maxOccurs="1" />
	 */
	private String cqlStr;
	
	
	public enum ValueExprType { // FIXME!!!!
		literal,
		min,
		max,
		pathExpr,
		Instance,
		Apply
	}

}
