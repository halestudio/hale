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
 * This class represents omwg:classConst. Used for building an expression that
 * constructs a FeatureClass based on other FeatureClasses.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class FeatureClassConstruction {

	/**
	 * Note: Interior element omwg:ClassAndType collapsed. Currently modeled as
	 * binary predicate between the elements of this.and. TODO MdV test whether
	 * nested and, or, not remains possible
	 * 
	 * <xs:element name="and" type="omwg:ClassAndType" minOccurs="0" />
	 */
	private List<FeatureClass> and;

	/**
	 * Note: Interior element omwg:ClassOrType collapsed. Currently modeled as
	 * binary predicate between the elements of this.or. TODO MdV test whether
	 * nested and, or, not remains possible
	 * 
	 * <xs:element name="or" type="omwg:ClassOrType" minOccurs="0" />
	 */
	private List<FeatureClass> or;

	/**
	 * Note: Interior element omwg:ClassNotType collapsed. Currently modeled as
	 * unary predicate. If more than one 'not' than like this: not(...) and not
	 * (...), or like this: not (... or ...)
	 * 
	 * <xs:element name="not" type="omwg:ClassNotType" minOccurs="0" />
	 */
	private FeatureClass not;

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
	public FeatureClass isNot() {
		return not;
	}

	/**
	 * @param not
	 *            the not to set
	 */
	public void setNot(FeatureClass not) {
		this.not = not;
	}

}
