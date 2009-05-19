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
	 * Note: Interior element omwg:ClassAndType collapsed. Currently modeled as
	 * binary predicate between this and the elements of this.and. 
	 * TODO MdV test whether nested and, or, not remains possible
	 * 
	 * <xs:element name="and" type="omwg:ClassAndType" minOccurs="0" />
	 */
	private List<FeatureClass> and;

	/**
	 * Note: Interior element omwg:ClassOrType collapsed. Currently modeled as
	 * binary predicate between this and the elements of this.or. 
	 * TODO MdV test whether nested and, or, not remains possible
	 * 
	 * <xs:element name="or" type="omwg:ClassOrType" minOccurs="0" />
	 */
	private List<FeatureClass> or;

	/**
	 * Note: Interior element omwg:ClassNotType collapsed. Currently modeled as
	 * unary predicate. TODO check the data type of 'not': is it a list of
	 * FeatureClasses or just one probably just one, and if more then one 'not'
	 * is needed, then like this: not(...) and not (...) or like this: not (...
	 * or ...) <xs:element name="not" type="omwg:ClassNotType" minOccurs="0" />
	 */
	private boolean not;

	/**
	 * Note: Interior element omwg:classConditionType collapsed. <xs:element
	 * ref="omwg:attributeValueCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeValueCondition;

	/**
	 * Note: Interior element omwg:classConditionType collapsed. <xs:element
	 * ref="omwg:attributeTypeCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeTypeCondition;

	/**
	 * Note: Interior element omwg:classConditionType collapsed. <xs:element
	 * ref="omwg:attributeOccurenceCondition" minOccurs="0"
	 * maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeOccurenceCondition;

	/**
	 * TODO add explanation
	 * <xs:group ref="omwg:transformation" minOccurs="0" maxOccurs="1" />
	 */
	private Function transf;

	/**
	 * TODO add explanation
	 */
	private Service service;

	// constructors ............................................................

	/**
	 * @param label
	 */
	public FeatureClass(List<String> label) {
		super(label);
	}

	// getters / setters .......................................................

	/**
	 * @return the and
	 */
	public List<FeatureClass> getAnd() {
		return and;
	}

	/**
	 * @param and
	 *            the and to set
	 */
	public void setAnd(List<FeatureClass> and) {
		this.and = and;
	}

	/**
	 * @return the or
	 */
	public List<FeatureClass> getOr() {
		return or;
	}

	/**
	 * @param or
	 *            the or to set
	 */
	public void setOr(List<FeatureClass> or) {
		this.or = or;
	}

	/**
	 * @return the not
	 */
	public boolean isNot() {
		return not;
	}

	/**
	 * @param not
	 *            the not to set
	 */
	public void setNot(boolean not) {
		this.not = not;
	}

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

	/**
	 * @return the transf
	 */
	public Function getTransf() {
		return transf;
	}

	/**
	 * @param transf
	 *            the transf to set
	 */
	public void setTransf(Function transf) {
		this.transf = transf;
	}

	/**
	 * @return the service
	 */
	public Service getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(Service service) {
		this.service = service;
	}

}
