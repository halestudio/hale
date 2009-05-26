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
	 * <xs:group ref="omwg:propConst" minOccurs="0" maxOccurs="1" />
	 */
	private PropertyConstruction propConstruction;

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

	// getters/setters .........................................................
	
	/**
	 * @return the propConstruction
	 */
	public PropertyConstruction getPropConstruction() {
		return propConstruction;
	}

	/**
	 * @param propConstruction the propConstruction to set
	 */
	public void setPropConstruction(PropertyConstruction propConstruction) {
		this.propConstruction = propConstruction;
	}

	/**
	 * @return the domainRestriction
	 */
	public List<FeatureClass> getDomainRestriction() {
		return domainRestriction;
	}

	/**
	 * @param domainRestriction the domainRestriction to set
	 */
	public void setDomainRestriction(List<FeatureClass> domainRestriction) {
		this.domainRestriction = domainRestriction;
	}

	/**
	 * @return the valueCondition
	 */
	public List<Restriction> getValueCondition() {
		return valueCondition;
	}

	/**
	 * @param valueCondition the valueCondition to set
	 */
	public void setValueCondition(List<Restriction> valueCondition) {
		this.valueCondition = valueCondition;
	}

	/**
	 * @return the typeCondition
	 */
	public List<String> getTypeCondition() {
		return typeCondition;
	}

	/**
	 * @param typeCondition the typeCondition to set
	 */
	public void setTypeCondition(List<String> typeCondition) {
		this.typeCondition = typeCondition;
	}

}
