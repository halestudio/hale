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
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class FeatureClass extends Entity {

	/**
	 * <xs:group ref="omwg:classConst" minOccurs="0" maxOccurs="1" />
	 */
	private FeatureClassConstruction classConstruction;

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
       * <xs:element ref="omwg:attributeOccurenceCondition" minOccurs="0"
	 * maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeOccurenceCondition;


	// constructors ............................................................

	/**
	 * @param label
	 */
	public FeatureClass(List<String> label) {
		super(label);
	}

	// getters / setters .......................................................


	/**
	 * @return the attributeValueCondition
	 */
	public List<Restriction> getAttributeValueCondition() {
		return attributeValueCondition;
	}

	/**
	 * @param attributeValueCondition
	 *            the attributeValueCondition to set
	 */
	public void setAttributeValueCondition(
			List<Restriction> attributeValueCondition) {
		this.attributeValueCondition = attributeValueCondition;
	}

	/**
	 * @return the attributeTypeCondition
	 */
	public List<Restriction> getAttributeTypeCondition() {
		return attributeTypeCondition;
	}

	/**
	 * @param attributeTypeCondition
	 *            the attributeTypeCondition to set
	 */
	public void setAttributeTypeCondition(
			List<Restriction> attributeTypeCondition) {
		this.attributeTypeCondition = attributeTypeCondition;
	}

	/**
	 * @return the attributeOccurenceCondition
	 */
	public List<Restriction> getAttributeOccurenceCondition() {
		return attributeOccurenceCondition;
	}

	/**
	 * @param attributeOccurenceCondition
	 *            the attributeOccurenceCondition to set
	 */
	public void setAttributeOccurenceCondition(
			List<Restriction> attributeOccurenceCondition) {
		this.attributeOccurenceCondition = attributeOccurenceCondition;
	}

	public void setClassConstruction(FeatureClassConstruction classConstruction) {
		this.classConstruction = classConstruction;
	}

	public FeatureClassConstruction getClassConstruction() {
		return classConstruction;
	}


}
