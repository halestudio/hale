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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
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
	 * Downcast path element
	 */
	private static class DowncastElement implements PathElement {

		private final Name elementName;
		
		private final TypeDefinition type;

		private final boolean unique;
		
		/**
		 * Constructor
		 * 
		 * @param elementName the name of the element the downcast is applied to
		 * @param type the definition of the type that is downcast to
		 * @param unique if the represented element cannot be repeated
		 */
		public DowncastElement(Name elementName, TypeDefinition type,
				boolean unique) {
			super();
			this.elementName = elementName;
			this.type = type;
			this.unique = unique;
		}

		/**
		 * @see PathElement#getName()
		 */
		@Override
		public Name getName() {
			return elementName;
		}

		/**
		 * @see PathElement#getType()
		 */
		@Override
		public TypeDefinition getType() {
			return type;
		}

		/**
		 * @see PathElement#isProperty()
		 */
		@Override
		public boolean isProperty() {
			return false;
		}

		/**
		 * @see PathElement#isDowncast()
		 */
		@Override
		public boolean isDowncast() {
			return true;
		}

		/**
		 * @see PathElement#isUnique()
		 */
		@Override
		public boolean isUnique() {
			return unique;
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((elementName == null) ? 0 : elementName.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		/**
		 * @see Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DowncastElement other = (DowncastElement) obj;
			if (elementName == null) {
				if (other.elementName != null)
					return false;
			} else if (!elementName.equals(other.elementName))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}

	}
	
	/**
	 * Sub-type path element
	 */
	private static class SubstitutionElement implements PathElement {

		private final SchemaElement element;
		
		private final boolean unique;
		
		/**
		 * Constructor
		 * 
		 * @param element the substitution element
		 * @param unique if the represented element cannot be repeated
		 */
		public SubstitutionElement(SchemaElement element, boolean unique) {
			this.element = element;
			this.unique = unique;
		}

		/**
		 * @see PathElement#getName()
		 */
		@Override
		public Name getName() {
			return element.getElementName();
		}

		/**
		 * @see PathElement#getType()
		 */
		@Override
		public TypeDefinition getType() {
			return element.getType();
		}

		/**
		 * @see PathElement#isProperty()
		 */
		@Override
		public boolean isProperty() {
			return false;
		}

		/**
		 * @see PathElement#isDowncast()
		 */
		@Override
		public boolean isDowncast() {
			return false;
		}

		/**
		 * @see PathElement#isUnique()
		 */
		@Override
		public boolean isUnique() {
			return unique;
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((element == null) ? 0 : element.hashCode());
			return result;
		}

		/**
		 * @see Object#equals(Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SubstitutionElement other = (SubstitutionElement) obj;
			if (element == null) {
				if (other.element != null)
					return false;
			} else if (!element.equals(other.element))
				return false;
			return true;
		}

	}

	/**
	 * A property path element 
	 */
	private static class PropertyElement implements PathElement {
		
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

		/**
		 * @see PathElement#isDowncast()
		 */
		@Override
		public boolean isDowncast() {
			return false;
		}

		/**
		 * @see PathElement#isUnique()
		 */
		@Override
		public boolean isUnique() {
			return attdef.getMaxOccurs() <= 1;
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((attdef == null) ? 0 : attdef.hashCode());
			return result;
		}

		/**
		 * @see Object#equals(Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PropertyElement other = (PropertyElement) obj;
			if (attdef == null) {
				if (other.attdef != null)
					return false;
			} else if (!attdef.equals(other.attdef))
				return false;
			return true;
		}

	}

	private final List<PathElement> steps = new ArrayList<PathElement>();
	
	private TypeDefinition lastType;
	
	private Name lastName;
	
	private GeometryWriter<?> geometryWriter;

	private boolean lastUnique;
	
	/**
	 * Create a definition path beginning with the given base path
	 * 
	 * @param basePath the base path
	 */
	public DefinitionPath(DefinitionPath basePath) {
		this(basePath.lastType, basePath.lastName, basePath.lastUnique);
		
		steps.addAll(basePath.getSteps());
	}

	/**
	 * Create an empty definition path
	 * 
	 * @param firstType the type starting the path 
	 * @param elementName the corresponding element name
	 * @param unique if the element starting the path cannot be repeated
	 */
	public DefinitionPath(TypeDefinition firstType, Name elementName, boolean unique) {
		super();
		
		lastType = firstType;
		lastName = elementName;
		lastUnique = unique; 
	}

	/**
	 * Add a substitution
	 * 
	 * @param element the substitution element
	 * 
	 * @return this path for chaining 
	 */
	public DefinitionPath addSubstitution(SchemaElement element) {
		// 1. sub-type must override previous sub-type
		// 2. sub-type must override a previous property XXX check this!!! or only the first?
		// XXX -> therefore removing the previous path element
		boolean unique = isLastUnique();
		
		if (steps.size() > 0) {
			steps.remove(steps.size() - 1);
		}
		
		addStep(new SubstitutionElement(element, unique));
		
		return this;
	}
	
	/**
	 * Add a downcast
	 * 
	 * @param subtype the definition of the sub-type that is to be cast to
	 * @return this path for chaining
	 */
	public DefinitionPath addDowncast(TypeDefinition subtype) {
		// 1. sub-type must override previous sub-type
		// 2. sub-type must override a previous property XXX check this!!! or only the first?
		// XXX -> therefore removing the previous path element
		Name elementName = getLastName();
		boolean unique = isLastUnique();
		
		if (steps.size() > 0) {
			steps.remove(steps.size() - 1);
		}
		
		addStep(new DowncastElement(elementName, subtype, unique));
		
		return this;
	}

	
	private void addStep(PathElement step) {
		steps.add(step);
		lastType = step.getType();
		lastName = step.getName();
		lastUnique = step.isUnique();
	}

	/**
	 * Add a property
	 * 
	 * @param property the property definition
	 * 
	 * @return this path for chaining 
	 */
	public DefinitionPath addProperty(AttributeDefinition property) {
		addStep(new PropertyElement(property));
		
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
		return Collections.unmodifiableList(steps);
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
	 * specified on creation
	 * 
	 * @return the last type
	 */
	public TypeDefinition getLastType() {
		return lastType;
	}
	
	/**
	 * Get the last name of the path. For empty paths this will be the name
	 * specified on creation
	 * 
	 * @return the last type
	 */
	public Name getLastName() {
		return lastName;
	}

	/**
	 * Get if the last element in the path is unique, which means that it cannot
	 * be repeated
	 * 
	 * @return if the last element in the path is unique, which means that it 
	 *   cannot be repeated
	 */
	public boolean isLastUnique() {
		return lastUnique;
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
				result.append(", "); //$NON-NLS-1$
			}
			
			result.append(step.getName());
		}
		
		if (result == null) {
			return "empty"; //$NON-NLS-1$
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
