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
public class FeatureClassConstruction 
	extends FeatureClass {

	/**
	 * Note: Interior element omwg:ClassAndType collapsed. Modeled as
	 * binary predicate between the elements of this.entities.
	 * 
	 * <xs:element name="and" type="omwg:ClassAndType" minOccurs="0" />
	 */
	private List<FeatureClass> entities;
	
	/**
	 * Indicates the operator/predicate valid for this 
	 * {@link FeatureClassConstruction}.
	 */
	private ConstructionType type;
	
	// constructors ............................................................
	
	public FeatureClassConstruction(List<String> label) {
		super(label);
		// TODO Auto-generated constructor stub
	}

	// getters / setters .......................................................
	
	/**
	 * @return the entities
	 */
	public List<FeatureClass> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<FeatureClass> entities) {
		this.entities = entities;
	}

	/**
	 * @return the type
	 */
	public ConstructionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ConstructionType type) {
		this.type = type;
	}

	public enum ConstructionType {
		AND, // intersection
		OR,
		NOT
	}

}
