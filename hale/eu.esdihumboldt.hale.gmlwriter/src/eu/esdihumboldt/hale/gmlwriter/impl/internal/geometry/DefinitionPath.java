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

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.gmlwriter.impl.internal.GmlWriterUtil;
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
	 * Sub-type path element
	 */
	private static class SubTypeElement implements PathElement {

		private final TypeDefinition subtype;
		
		/**
		 * Constructor
		 * 
		 * @param subtype the sub-type
		 */
		public SubTypeElement(TypeDefinition subtype) {
			this.subtype = subtype;
		}

		/**
		 * @see PathElement#getName()
		 */
		@Override
		public Name getName() {
			return GmlWriterUtil.getElementName(subtype);
		}

		/**
		 * @see PathElement#getType()
		 */
		@Override
		public TypeDefinition getType() {
			return subtype;
		}

		/**
		 * @see PathElement#isProperty()
		 */
		@Override
		public boolean isProperty() {
			return false;
		}

	}

	/**
	 * A property path element 
	 */
	public static class PropertyElement implements PathElement {
		
		private final AttributeDefinition attdef;

		/**
		 * Constructor
		 * 
		 * @param attdef the attribute definition
		 */
		public PropertyElement(AttributeDefinition attdef) {
			this.attdef = attdef;
		}

		/**
		 * @see PathElement#getName()
		 */
		public Name getName() {
			return new NameImpl(attdef.getNamespace(), attdef.getName());
		}
		
		/**
		 * @see PathElement#getType()
		 */
		public TypeDefinition getType() {
			return attdef.getAttributeType();
		}
		
		/**
		 * @see PathElement#isProperty()
		 */
		public boolean isProperty() {
			return true;
		}

	}

	private final List<PathElement> steps = new ArrayList<PathElement>();
	
	private TypeDefinition lastType;
	
	private GeometryWriter<?> geometryWriter;
	
	/**
	 * Create a definition path beginning with the given base path
	 * 
	 * @param basePath the base path
	 */
	public DefinitionPath(DefinitionPath basePath) {
		this(basePath.lastType);
		
		steps.addAll(basePath.getSteps());
	}

	/**
	 * Create an empty definition path
	 * 
	 * @param firstType the type starting the path 
	 */
	public DefinitionPath(TypeDefinition firstType) {
		super();
		
		lastType = firstType;
	}

	/**
	 * Add a sub-type
	 * 
	 * @param type the sub-type
	 * 
	 * @return this path for chaining 
	 */
	public DefinitionPath addSubType(TypeDefinition type) {
		// 1. sub-type must override previous sub-type
		// 2. sub-type must override a previous property XXX check this!!! or only the first?
		// XXX -> there removing the previous path element
		if (steps.size() > 0) {
			steps.remove(steps.size() - 1);
		}
		
		steps.add(new SubTypeElement(type));
		lastType = type;
		
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
		steps.add(new PropertyElement(property));
		lastType = property.getAttributeType();
		
		return this;
	}

	/**
	 * @return the geometryWriter
	 */
	public GeometryWriter<?> getGeometryWriter() {
		return geometryWriter;
	}

	/**
	 * @param geometryWriter the geometryWriter to set
	 */
	public void setGeometryWriter(GeometryWriter<?> geometryWriter) {
		this.geometryWriter = geometryWriter;
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
	 * Get the last type of the path. For empty paths this will be the type
	 * specified in creation
	 * 
	 * @return the last type
	 */
	public TypeDefinition getLastType() {
		return lastType;
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

	/**
	 * Get the last path element
	 * 
	 * @return the last path element or <code>null</code> if it's empty
	 */
	public PathElement getLastElement() {
		if (steps.isEmpty()) {
			return null;
		}
		return steps.get(steps.size() - 1);
	}
	
}
