/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;

/**
 * Represents a path in a type definition hierarchy (regarding subtypes and
 * properties)
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefinitionPath {

	/**
	 * Downcast path element
	 */
	private static class DowncastElement implements PathElement {

		private final QName elementName;

		private final TypeDefinition type;

		private final boolean unique;

		/**
		 * Constructor
		 * 
		 * @param elementName the name of the element the downcast is applied to
		 * @param type the definition of the type that is downcast to
		 * @param unique if the represented element cannot be repeated
		 */
		public DowncastElement(QName elementName, TypeDefinition type, boolean unique) {
			super();
			this.elementName = elementName;
			this.type = type;
			this.unique = unique;
		}

		/**
		 * @see PathElement#getName()
		 */
		@Override
		public QName getName() {
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
		 * @see PathElement#prepareWrite(XMLStreamWriter)
		 */
		@Override
		public void prepareWrite(XMLStreamWriter writer) throws XMLStreamException {
			// add xsi:type
			writer.writeAttribute(StreamGmlWriter.SCHEMA_INSTANCE_NS, "type", getType().getName()
					.getLocalPart()); // XXX namespace needed for
										// the attribute value?
		}

		/**
		 * @see PathElement#isUnique()
		 */
		@Override
		public boolean isUnique() {
			return unique;
		}

		/**
		 * @see PathElement#isTransient()
		 */
		@Override
		public boolean isTransient() {
			return false;
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((elementName == null) ? 0 : elementName.hashCode());
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
			}
			else if (!elementName.equals(other.elementName))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			}
			else if (!type.equals(other.type))
				return false;
			return true;
		}

	}

//	/**
//	 * Sub-type path element
//	 */
//	private static class SubstitutionElement implements PathElement {
//
//		private final SchemaElement element;
//		
//		private final boolean unique;
//		
//		/**
//		 * Constructor
//		 * 
//		 * @param element the substitution element
//		 * @param unique if the represented element cannot be repeated
//		 */
//		public SubstitutionElement(SchemaElement element, boolean unique) {
//			this.element = element;
//			this.unique = unique;
//		}
//
//		/**
//		 * @see PathElement#getName()
//		 */
//		@Override
//		public Name getName() {
//			return element.getElementName();
//		}
//
//		/**
//		 * @see PathElement#getType()
//		 */
//		@Override
//		public TypeDefinition getType() {
//			return element.getType();
//		}
//
//		/**
//		 * @see PathElement#isProperty()
//		 */
//		@Override
//		public boolean isProperty() {
//			return false;
//		}
//
//		/**
//		 * @see PathElement#isDowncast()
//		 */
//		@Override
//		public boolean isDowncast() {
//			return false;
//		}
//
//		/**
//		 * @see PathElement#isUnique()
//		 */
//		@Override
//		public boolean isUnique() {
//			return unique;
//		}
//
//		/**
//		 * @see Object#hashCode()
//		 */
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result
//					+ ((element == null) ? 0 : element.hashCode());
//			return result;
//		}
//
//		/**
//		 * @see Object#equals(Object)
//		 */
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			SubstitutionElement other = (SubstitutionElement) obj;
//			if (element == null) {
//				if (other.element != null)
//					return false;
//			} else if (!element.equals(other.element))
//				return false;
//			return true;
//		}
//
//	}

	/**
	 * Path element representing a group. XXX a {@link DefinitionPath} that is
	 * used for writing should never end on a {@link GroupElement}!
	 * 
	 * @author Simon Templer
	 */
	private static class GroupElement implements PathElement {

		private final GroupPropertyDefinition groupDef;

		/**
		 * Create a path element representing a group.
		 * 
		 * @param groupDef the group property definition
		 */
		public GroupElement(GroupPropertyDefinition groupDef) {
			super();
			this.groupDef = groupDef;
		}

		/**
		 * @see PathElement#getName()
		 */
		@Override
		public QName getName() {
			return groupDef.getName();
		}

		/**
		 * @see PathElement#getType()
		 */
		@Override
		public TypeDefinition getType() {
			// no type for a group
			return null;
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
			long max = groupDef.getConstraint(Cardinality.class).getMaxOccurs();
			return max != Cardinality.UNBOUNDED && max <= 1;
		}

		/**
		 * @see PathElement#isTransient()
		 */
		@Override
		public boolean isTransient() {
			return true;
		}

		@Override
		public void prepareWrite(XMLStreamWriter writer) throws XMLStreamException {
			// do nothing
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((groupDef == null) ? 0 : groupDef.hashCode());
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GroupElement other = (GroupElement) obj;
			if (groupDef == null) {
				if (other.groupDef != null)
					return false;
			}
			else if (!groupDef.equals(other.groupDef))
				return false;
			return true;
		}

	}

	/**
	 * A property path element
	 */
	private static class PropertyElement implements PathElement {

		private final PropertyDefinition propDef;

		/**
		 * Constructor
		 * 
		 * @param attdef the attribute definition
		 */
		public PropertyElement(PropertyDefinition attdef) {
			this.propDef = attdef;
		}

		/**
		 * @see PathElement#getName()
		 */
		@Override
		public QName getName() {
			return propDef.getName();
		}

		/**
		 * @see PathElement#getType()
		 */
		@Override
		public TypeDefinition getType() {
			return propDef.getPropertyType();
		}

		/**
		 * @see PathElement#isProperty()
		 */
		@Override
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
			long max = propDef.getConstraint(Cardinality.class).getMaxOccurs();
			return max != Cardinality.UNBOUNDED && max <= 1;
		}

		/**
		 * @see PathElement#isTransient()
		 */
		@Override
		public boolean isTransient() {
			return false;
		}

		@Override
		public void prepareWrite(XMLStreamWriter writer) throws XMLStreamException {
			// do nothing
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((propDef == null) ? 0 : propDef.hashCode());
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
			if (propDef == null) {
				if (other.propDef != null)
					return false;
			}
			else if (!propDef.equals(other.propDef))
				return false;
			return true;
		}

	}

	private final List<PathElement> steps = new ArrayList<PathElement>();

	private TypeDefinition lastType;

	private QName lastName;

	private GeometryWriter<?> geometryWriter;

	private boolean lastUnique;

	private TypeDefinition geometryCompatibleType;

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
	public DefinitionPath(TypeDefinition firstType, QName elementName, boolean unique) {
		super();

		lastType = firstType;
		lastName = elementName;
		lastUnique = unique;
	}

	/**
	 * Create a path with at least one element.
	 * 
	 * @param elements the path elements
	 */
	public DefinitionPath(List<PathElement> elements) {
		this(elements.get(elements.size() - 1).getType(), elements.get(elements.size() - 1)
				.getName(), elements.get(elements.size() - 1).isUnique());

		steps.addAll(elements);
	}

//	/**
//	 * Add a substitution
//	 * 
//	 * @param element the substitution element
//	 * 
//	 * @return this path for chaining 
//	 */
//	public DefinitionPath addSubstitution(SchemaElement element) {
//		// 1. sub-type must override previous sub-type
//		// 2. sub-type must override a previous property XXX check this!!! or only the first?
//		// XXX -> therefore removing the previous path element
//		boolean unique = isLastUnique();
//		
//		if (steps.size() > 0) {
//			steps.remove(steps.size() - 1);
//		}
//		
//		addStep(new SubstitutionElement(element, unique));
//		
//		return this;
//	}

	/**
	 * Add a downcast
	 * 
	 * @param subtype the definition of the sub-type that is to be cast to
	 * @return this path for chaining
	 */
	public DefinitionPath addDowncast(TypeDefinition subtype) {
		// 1. sub-type must override previous sub-type
		// 2. sub-type must override a previous property XXX check this!!! or
		// only the first?
		// XXX -> therefore removing the previous path element
		QName elementName = getLastName();
		boolean unique = isLastUnique();

		if (steps.size() > 0) {
			steps.remove(steps.size() - 1);
		}

		addStep(new DowncastElement(elementName, subtype, unique));

		return this;
	}

	/**
	 * Add a group to the path
	 * 
	 * @param groupDef the group definition
	 * @return this path for chaining
	 */
	public DefinitionPath addGroup(GroupPropertyDefinition groupDef) {
		steps.add(new GroupElement(groupDef));
		return this;
	}

	/**
	 * Add a child to the path
	 * 
	 * @param child the child, either a group or property
	 * @return this path for chaining
	 */
	public DefinitionPath add(ChildDefinition<?> child) {
		if (child.asGroup() != null) {
			return addGroup(child.asGroup());
		}
		if (child.asProperty() != null) {
			return addProperty(child.asProperty());
		}
		throw new IllegalArgumentException("Supplied an invalif child definition.");
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
	public DefinitionPath addProperty(PropertyDefinition property) {
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
	 * @return the geometryCompatibleType
	 */
	public TypeDefinition getGeometryCompatibleType() {
		return geometryCompatibleType;
	}

	/**
	 * @param geometryWriter the geometryWriter to set
	 * @param geometryCompatibleType the type that was identified as compatible
	 *            type by the writer
	 */
	public void setGeometryWriter(GeometryWriter<?> geometryWriter,
			TypeDefinition geometryCompatibleType) {
		this.geometryWriter = geometryWriter;
		this.geometryCompatibleType = geometryCompatibleType;
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
	 * specified on creation XXX not for groups
	 * 
	 * @return the last type
	 */
	public TypeDefinition getLastType() {
		return lastType;
	}

	/**
	 * Get the last name of the path. For empty paths this will be the name
	 * specified on creation XXX not if last is a group
	 * 
	 * @return the last type
	 */
	public QName getLastName() {
		return lastName;
	}

	/**
	 * Get if the last element in the path is unique, which means that it cannot
	 * be repeated XXX not if last is a group
	 * 
	 * @return if the last element in the path is unique, which means that it
	 *         cannot be repeated
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
