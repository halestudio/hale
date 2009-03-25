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

import eu.esdihumboldt.goml.align.Entity;

/**
 * This class represents the <xs:complexType name="ClassType">. Some interior 
 * types have been collapsed to keep the number of classes to the required 
 * minimum.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureClass extends Entity {
	
	/**
	 * Note: Interior element omwg:label collapsed.
	 * <xs:element ref="omwg:label" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<String> label;
	
	/**
	 * Note: Interior element omwg:ClassAndType collapsed.
	 * <xs:element name="and" type="omwg:ClassAndType" minOccurs="0"  />
	 */
	private List<FeatureClass> and;
	
	/**
	 * Note: Interior element omwg:ClassOrType collapsed.
	 * <xs:element name="or" type="omwg:ClassOrType" minOccurs="0"  />
	 */
	private List<FeatureClass> or;
	
	/**
	 * Note: Interior element omwg:ClassNotType collapsed.
	 * <xs:element name="not" type="omwg:ClassNotType" minOccurs="0"  />
	 */
	private List<FeatureClass> not;
	
	/**
	 * Note: Interior element omwg:classConditionType collapsed.
	 * <xs:element ref="omwg:attributeValueCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeValueCondition;
	
	/**
	 * Note: Interior element omwg:classConditionType collapsed.
	 * <xs:element ref="omwg:attributeTypeCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeTypeCondition;
	
	/**
	 * Note: Interior element omwg:classConditionType collapsed.
	 * <xs:element ref="omwg:attributeOccurenceCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeOccurenceCondition;
	
	/**
	 * <xs:group ref="omwg:transformation" minOccurs="0" maxOccurs="1"   />
	 */
	private Transformation transformation;

}
