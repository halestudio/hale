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
 * This class represents omwg:propConst. Used for building an expression that
 * constructs a Property based on other Entities (other Properties or Relations)
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class PropertyConstruction 
	extends Property {

	/**
	 * The {@link List} of {@link Property} objects bound by this 
	 * {@link PropertyConstructionType}.
	 * <xs:group ref="omwg:propConst" minOccurs="0" maxOccurs="1" />
	 */
	private List<Property> properties;
	
	/**
	 * The {@link PropertyConstructionType} of this {@link PropertyConstruction}.
	 */
	private PropertyConstructionType type;


	public PropertyConstruction(List<String> label) {
		super(label);
		// TODO Auto-generated constructor stub
	}
	
	// getters / setters .......................................................
	
	/**
	 * @return the properties
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	/**
	 * @return the type
	 */
	public PropertyConstructionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PropertyConstructionType type) {
		this.type = type;
	}

	public enum PropertyConstructionType {
		AND,
		OR,
		NOT,
		FIRST,
		NEXT
	}

}
