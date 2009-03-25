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
 * This class represents the <xs:group name="propConst">. It is modeled as a
 * object-oriented shorthand to summarize multiple choice options:
 * 
 *    <xs:group name="propConst">
 *           <xs:choice>
 *              <xs:element name="and" type="omwg:PropAndType" />
 *              <xs:element name="or" type="omwg:PropOrType" />
 *              <xs:element name="not" type="omwg:PropNotType" />
 *              <xs:element name="first" type="omwg:PropExprType" />
 *              <xs:element name="next" type="omwg:RelExprType" />
 *           </xs:choice>
 *   </xs:group>
 *   
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PropertyExpression {

	/**
	 * <xs:element name="Collection">
	 *    <xs:complexType>
	 *       <xs:sequence>
	 *          <xs:element name="item" type="omwg:PropItemType" maxOccurs="unbounded" />
	 *       </xs:sequence>
	 *    </xs:complexType>
	 * </xs:element>
	 */
	private List<Property> properties;
	
	/**
	 * Instead of subtyping, a member declares the specific PropertyExpression's 
	 * type.
	 */
	private ExpressionType type;
	
	/**
	 * 
	 */
	public enum ExpressionType {
		and,
		or,
		not,
		first,
		next
	}
	
}
