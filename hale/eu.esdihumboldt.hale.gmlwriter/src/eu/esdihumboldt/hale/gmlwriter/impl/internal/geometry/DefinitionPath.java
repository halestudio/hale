/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Represents a path in a type definition hierarchy (regarding subtypes 
 * and properties)
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefinitionPath {

	/**
	 * A path element 
	 */
	public class PathElement {
		
		private final String name;
		private final TypeDefinition type;
		private final boolean property;
		
		/**
		 * Constructor
		 * 
		 * @param name the path element name
		 * @param type the path element type definition
		 * @param property if the path element represents a property
		 */
		public PathElement(String name, TypeDefinition type, boolean property) {
			super();
			this.name = name;
			this.type = type;
			this.property = property;
		}

		/**
		 * Get the path element name. This is either a property or a subtype
		 * name
		 * 
		 * @return the element name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Get the path element type definition
		 * 
		 * @return the path element type definition
		 */
		public TypeDefinition getType() {
			return type;
		}
		
		/**
		 * Determines if this path element represents a property, otherwise it
		 * represents a sub-type
		 * 
		 * @return if this path element represents a property
		 */
		public boolean isProperty() {
			return property;
		}

	}

	private final List<PathElement> steps = new ArrayList<PathElement>();
	
	/**
	 * Create a definition path beginning with the given base path
	 * 
	 * @param basePath the base path
	 */
	public DefinitionPath(DefinitionPath basePath) {
		this();
		
		steps.addAll(basePath.getSteps());
	}

	/**
	 * Create an empty definition path
	 */
	public DefinitionPath() {
		super();
	}

	/**
	 * Add a sub-type
	 * 
	 * @param type the sub-type
	 * 
	 * @return this path for chaining 
	 */
	public DefinitionPath addSubType(TypeDefinition type) {
		steps.add(new PathElement(type.getName().getLocalPart(), type, false));
		
		return this;
	}
	
	/**
	 * Add a property
	 * 
	 * @param property the property definition
	 * 
	 * @return this path for chaining 
	 */
	public DefinitionPath addProperty(AttributeDefinition property) {
		steps.add(new PathElement(property.getName(), 
				property.getAttributeType(), true));
		
		return this;
	}

	/**
	 * @return the steps
	 */
	public List<PathElement> getSteps() {
		return steps;
	}
	
	/**
	 * Determines if the path is empty
	 * 
	 * @return if the path is empty
	 */
	public boolean isEmpty() {
		return steps.isEmpty();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer result = null;
		for (PathElement step : steps) {
			if (result == null) {
				result = new StringBuffer();
			}
			else {
				result.append(", ");
			}
			
			result.append(step.getName());
		}
		
		if (result == null) {
			return "empty";
		}
		else {
			return result.toString();
		}
	}
	
}
