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
 * This class represents omwg:PropertyType.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class Property 
	extends Entity {

	/**
	 * <xs:element ref="omwg:label" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<String> label;

	/**
	 * <xs:group ref="omwg:propConst" minOccurs="0" maxOccurs="1" />
	 */
	// private PropertyExpression propConst;
	private List<Property> and;
	private List<Property> or;
	private Property not;
	private Relation first;
	private Property next;

	/**
	 * <xs:group ref="omwg:propCond" minOccurs="0" maxOccurs="unbounded" /> In
	 * stead of the group use the group members directly
	 */
	// private List<PropertyCondition> propCond;

	/**
	 * <xs:element ref="omwg:domainRestriction" minOccurs="0"
	 * maxOccurs="unbounded" />
	 */
	private List<FeatureClass> domainRestriction;

	/**
	 * <xs:element ref="omwg:valueCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> valueCondition;

	/**
	 * TODO add explanation
	 * TODO: use actual geometry classes from GeoAPI instead of String.
	 * 
	 * <xs:element ref="omwg:typeCondition" minOccurs="0" maxOccurs="unbounded"
	 * />
	 */
	private List<String> typeCondition;
	
	// constructors ............................................................
	
	public Property(List<String> label) {
		super(label);
	}
	
	

}
