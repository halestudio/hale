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

package eu.esdihumboldt.hale.schemaprovider.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.geotools.feature.NameImpl;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.rdf.About;

/**
 * Represents a type definition
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public class TypeDefinition extends AbstractDefinition implements
		Comparable<TypeDefinition>, Definition {

	private static final Logger log = Logger.getLogger(TypeDefinition.class);

	/**
	 * The type name
	 */
	private final Name name;

	/**
	 * The feature type representing the type
	 */
	private AttributeType type;

	/**
	 * The super type definition
	 */
	private final TypeDefinition superType;

	/**
	 * The subtypes
	 */
	private final SortedSet<TypeDefinition> subTypes = new TreeSet<TypeDefinition>();

	/**
	 * The elements referencing this type
	 */
	private final Set<SchemaElement> declaringElements = new HashSet<SchemaElement>();

	/**
	 * If the type is abstract
	 */
	private boolean abstractType = false;

	private final boolean complex;

	/**
	 * If the super type relation is a restriction
	 */
	private final boolean restriction;

	/**
	 * The list of declared attributes (list because order must be maintained
	 * for writing)
	 */
	private final List<AttributeDefinition> declaredAttributes = new ArrayList<AttributeDefinition>();

	/**
	 * The inherited attributes
	 */
	private List<AttributeDefinition> inheritedAttributes;

	/**
	 * Create a new type definition
	 * 
	 * @param name
	 *            the type name
	 * @param type
	 *            the corresponding feature type, may be <code>null</code>
	 * @param superType
	 *            the super type, may be <code>null</code>
	 */
	public TypeDefinition(Name name, AttributeType type,
			TypeDefinition superType) {
		this(name, type, superType, false);
	}

	/**
	 * Create a new type definition
	 * 
	 * @param name
	 *            the type name
	 * @param type
	 *            the corresponding feature type, may be <code>null</code>
	 * @param superType
	 *            the super type, may be <code>null</code>
	 * @param restriction
	 *            if the super type relation is a restriction
	 */
	public TypeDefinition(Name name, AttributeType type,
			TypeDefinition superType, boolean restriction) {
		super();

		this.restriction = restriction;

		if (name == null && type != null) {
			this.name = new NameImpl(type.getName().getNamespaceURI(), type
					.getName().getLocalPart());
		} else {
			this.name = name;
		}
		this.type = type;
		this.superType = superType;

		if (type != null && !(type instanceof FeatureType)) {
			complex = false;
		} else {
			complex = true;
		}

		if (superType != null) {
			superType.subTypes.add(this);
		}

		if (type != null) {
			this.setAbstract(type.isAbstract());
		}

		if (this.name != null) {
			// special cases TODO refactor/outsource?

			// ReferenceType
			// if
			// (this.name.getNamespaceURI().startsWith("http://www.opengis.net/gml/")
			// && this.name.getLocalPart().equals("ReferenceType")) {
			// Name hrefName = new NameImpl("http://www.w3.org/2001/XMLSchema",
			// "anyURI");
			// TypeDefinition hrefType = new TypeDefinition(hrefName,
			// XSSchema.ANYURI_TYPE, null);
			// AttributeDefinition hrefAttribute = new
			// CustomDefaultAttribute("href", hrefName , hrefType,
			// "http://www.w3.org/1999/xlink");
			// addDeclaredAttribute(hrefAttribute);
			// Collection<String> values = new ArrayList<String>();
			// values.add("none");
			// values.add("simple");
			// values.add("resource");
			// values.add("extended");
			// values.add("locator");
			// values.add("arc");
			// values.add("title");
			// Name typeName = new NameImpl("http://www.w3.org/2001/XMLSchema",
			// "string");
			// TypeDefinition typeType = new TypeDefinition(typeName, new
			// EnumAttributeTypeImpl(XSSchema.STRING_TYPE, values , false,
			// null), null);
			// AttributeDefinition typeAttribute = new
			// CustomDefaultAttribute("type",
			// typeName, typeType, "http://www.w3.org/1999/xlink");
			// addDeclaredAttribute(typeAttribute);
			// }
		}
	}

	/**
	 * Determines if this type actually represents a feature type that is based
	 * on AbstractFeatureType
	 * 
	 * @return if this definition represents a feature type
	 */
	public boolean isFeatureType() {
		AttributeType type = getType(null);

		if (name.getLocalPart().equalsIgnoreCase("AbstractFeatureType")) { //$NON-NLS-1$
			return true;
		} else if (type != null && !(type instanceof FeatureType)) {
			return false;
		} else if (getSuperType() == null) {
			return false;
		} else {
			return getSuperType().isFeatureType();
		}
	}

	/**
	 * Determines if the type has a geometry attribute
	 * 
	 * @return if the type has a geometry attribute
	 */
	public boolean hasGeometry() {
		AttributeType type = getType(null);

		return type instanceof FeatureType
				&& ((FeatureType) type).getGeometryDescriptor() != null;
	}

	/**
	 * Determines if this type represents a complex type
	 * 
	 * @return if this definition represents a complex type
	 */
	public boolean isComplexType() {
		return complex;
	}

	/**
	 * Add a declared attribute, the declaring type of the attribute will be set
	 * to this type
	 * 
	 * @param attribute
	 *            the attribute definition
	 */
	public void addDeclaredAttribute(AttributeDefinition attribute) {
		attribute.setDeclaringType(this);

		int idx = declaredAttributes.indexOf(attribute);
		if (idx >= 0) {
			// replace
			declaredAttributes.remove(idx);
			declaredAttributes.add(idx, attribute);
		} else {
			declaredAttributes.add(attribute);
		}
	}

	/**
	 * Removes a declared attribute
	 * 
	 * @param attribute
	 *            the attribute to remove
	 */
	public void removeDeclaredAttribute(AttributeDefinition attribute) {
		attribute.setDeclaringType(null);
		declaredAttributes.remove(attribute);
	}

	/**
	 * Add an element that references this type
	 * 
	 * @param element
	 *            the element that references this type
	 */
	public void addDeclaringElement(SchemaElement element) {
		declaringElements.add(element);
	}

	/**
	 * Removes an element that references this type
	 * 
	 * @param element
	 *            the element to remove
	 */
	public void removeDeclaringElement(SchemaElement element) {
		declaringElements.remove(element);
	}

	/**
	 * @return the name
	 */
	public Name getName() {
		return name;
	}

	/**
	 * 
	 * @param resolving
	 *            the types that are already in the process of creating a
	 *            feature type, may be <code>null</code>
	 * 
	 * @return the featureType
	 */
	public AttributeType getType(Set<TypeDefinition> resolving) {
		if (type == null) {
			if (!declaringElements.isEmpty()) {
				// XXX grab first
				SchemaElement element = declaringElements.iterator().next();
				return element.getAttributeType(resolving);
			} else {
				type = createFeatureType(null, resolving);
			}
		}
		return type;
	}

	/**
	 * Get the feature type if available
	 * 
	 * @return the feature type or <code>null</code> if no type could be
	 *         determined or the type is not a feature type
	 */
	public FeatureType getFeatureType() {
		AttributeType type = getType(null);
		if (type != null && type instanceof FeatureType) {
			return (FeatureType) type;
		} else {
			return null;
		}
	}

	/**
	 * Create the feature type from the super types and attributes, this method
	 * will be called when there was no explicit type provided
	 * 
	 * @param name
	 *            a custom name to use for the type (e.g. the element name) or
	 *            <code>null</code>
	 * 
	 * @param resolving
	 *            the types that are already in the process of creating a
	 *            feature type
	 * 
	 * @return the feature type
	 */
	public FeatureType createFeatureType(Name name,
			Set<TypeDefinition> resolving) {
		SimpleFeatureTypeBuilderThatHasNoSillySuperTypeRestriction builder = new SimpleFeatureTypeBuilderThatHasNoSillySuperTypeRestriction();

		if (resolving == null) {
			resolving = new HashSet<TypeDefinition>();
		}
		resolving.add(this);
		// a new set based on resolving has to be created for each resolve path

		if (getSuperType() != null) {
			// has super type
			builder.setSuperType(getSuperType().getType(
					new HashSet<TypeDefinition>(resolving)));
		} else {
			builder.setSuperType(null);
		}

		// add all attributes
		TypeDefinition typeDef = this;
		List<AttributeDescriptor> geometryCandidates = new ArrayList<AttributeDescriptor>();
		while (typeDef != null) {
			// add attributes for current type
			for (AttributeDefinition attribute : typeDef
					.getDeclaredAttributes()) {
				TypeDefinition attrType = attribute.getAttributeType();
				if (this.equals(attrType)) {
					log.warn("Self referencing type: " + getName()); //$NON-NLS-1$
				} else {
					AttributeDescriptor desc = attribute
							.createAttributeDescriptor(new HashSet<TypeDefinition>(
									resolving));
					if (desc != null) {
						builder.add(desc);

						if (Geometry.class.isAssignableFrom(desc.getType()
								.getBinding())) {
							geometryCandidates.add(desc);
						}
					}
				}
			}

			// switch to super type
			typeDef = typeDef.getSuperType();
		}

		AttributeDescriptor geoDesc = getDefaultGeometryDescriptor(geometryCandidates);
		if (geoDesc != null) {
			builder.setDefaultGeometry(geoDesc.getName().getLocalPart());
		}

		// other properties
		builder.setAbstract(abstractType);

		builder.setName((name != null) ? (name) : (getName()));
		return builder.buildFeatureType();
	}

	/**
	 * Get the preferred geometry attribute descriptor for the given set of
	 * candidates
	 * 
	 * @param geometryCandidates
	 *            the available geometry descriptors
	 * 
	 * @return the preferred geometry descriptor
	 */
	private AttributeDescriptor getDefaultGeometryDescriptor(
			List<AttributeDescriptor> geometryCandidates) {
		if (geometryCandidates == null || geometryCandidates.isEmpty()) {
			return null;
		} else if (geometryCandidates.size() == 1) {
			return geometryCandidates.iterator().next();
		} else {
			Collections.sort(geometryCandidates,
					new Comparator<AttributeDescriptor>() {

						@Override
						public int compare(AttributeDescriptor o1,
								AttributeDescriptor o2) {
							int result = 0;

							String name1 = o1.getLocalName();
							String name2 = o2.getLocalName();

							IDefaultGeometries defaultGeometries;
							try {
								Class<?> dfImpl = Class
										.forName("eu.esdihumboldt.hale.schemaprovider.uiconfig.DefaultGeometries");
								defaultGeometries = (IDefaultGeometries) dfImpl
										.newInstance();
							} catch (Exception e) {
								defaultGeometries = DefaultGeometries
										.getInstance();
							}

							String defName = defaultGeometries
									.getDefaultGeometryName(getName());
							if (defName != null) {
								// name from preferences check

								if (name1.equals(defName)) {
									result = -1;
								} else if (name2.equals(defName)) {
									result = 1;
								}
							}

							if (result == 0) {
								// name check

								// prefer properties name geometry - XXX are
								// there any default names for default
								// geometries?
								if (name1.equals("geometry")) { //$NON-NLS-1$
									result = -1;
								} else if (name2.equals("geometry")) { //$NON-NLS-1$
									result = 1;
								}
							}

							Class<?> bind1 = o1.getType().getBinding();
							Class<?> bind2 = o2.getType().getBinding();

							if (result == 0) {
								// binding check

								// prefer Geometry binding to more concrete
								// binding
								if (bind1.equals(Geometry.class)
										&& !bind2.equals(Geometry.class)) {
									result = -1;
								} else if (!bind1.equals(Geometry.class)
										&& bind2.equals(Geometry.class)) {
									result = 1;
								}
							}

							if (result == 0) {
								// fall back to alphabetical order
								return name1.compareTo(name2);
							}

							return result;
						}
					});

			return geometryCandidates.iterator().next();
		}
	}

	/**
	 * @return the superType
	 */
	public TypeDefinition getSuperType() {
		return superType;
	}

	/**
	 * @return the declaredAttributes
	 */
	public Iterable<AttributeDefinition> getDeclaredAttributes() {
		return declaredAttributes;
	}

	/**
	 * Get the declared attributes and the super type attributes sorted
	 * alphabetically
	 * 
	 * @return the attribute definitions
	 */
	public SortedSet<AttributeDefinition> getSortedAttributes() {
		return new TreeSet<AttributeDefinition>(getAttributes());
	}

	/**
	 * Get the declared attributes and the super type attributes
	 * 
	 * @return the attribute definitions
	 */
	public Collection<AttributeDefinition> getAttributes() {
		Collection<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

		if (!restriction) { // FIXME for now we assume that for a restriction
							// all properties are redefined - let's how we fare
							// with that
			if (inheritedAttributes == null) {
				inheritedAttributes = new ArrayList<AttributeDefinition>();

				// populate inherited attributes
				TypeDefinition parent = getSuperType();
				while (parent != null) {
					for (AttributeDefinition parentAttribute : parent
							.getDeclaredAttributes()) {
						// create attribute definition copy
						AttributeDefinition attribute = parentAttribute
								.copyAttribute(this);
						inheritedAttributes.add(attribute);
					}

					parent = parent.getSuperType();
				}
			}

			// add inherited attributes
			attributes.addAll(inheritedAttributes);
		}

		// add declared attributes afterwards - correct order for output
		attributes.addAll(declaredAttributes);

		return attributes;
	}

	/**
	 * Get if the relation to the super type is a restriction
	 * 
	 * @return the restriction
	 */
	public boolean isRestriction() {
		return restriction;
	}

	/**
	 * @return the abstractType
	 */
	public boolean isAbstract() {
		return abstractType;
	}

	/**
	 * @param abstractType
	 *            the abstractType to set
	 */
	public void setAbstract(boolean abstractType) {
		this.abstractType = abstractType;
	}

	/**
	 * @return the subTypes
	 */
	public Collection<TypeDefinition> getSubTypes() {
		return subTypes;
	}

	/**
	 * Get the schema elements representing sub-types of this type definition
	 * that may substitute the given property.
	 * 
	 * @param elementName
	 *            the name of the element to substitute
	 * 
	 * @return the schema elements that may substitute the property
	 */
	public Collection<SchemaElement> getSubstitutions(Name elementName) {
		if (elementName == null) {
			// no substitution allowed?
			return new ArrayList<SchemaElement>();
		} else {
			List<SchemaElement> result = new ArrayList<SchemaElement>();
			for (TypeDefinition type : subTypes) {
				Set<SchemaElement> elements = type.getDeclaringElements();

				// substitutions for subtypes
				result.addAll(type.getSubstitutions(elementName));

				for (SchemaElement element : elements) {
					// check if element may substitute the property
					if (element.getSubstitutionGroup() != null
							&& element.getSubstitutionGroup().equals(
									elementName)) {
						result.add(element);
					}
				}
			}

			return result;
		}
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Two type definitions are equal if their name is equal (namespace and
	 * local part)
	 * 
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
		TypeDefinition other = (TypeDefinition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.getNamespaceURI().equals(other.name.getNamespaceURI()))
			return false;
		else if (!name.getLocalPart().equals(other.name.getLocalPart()))
			return false;
		return true;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(TypeDefinition other) {
		int result = name.getLocalPart().compareToIgnoreCase(
				other.name.getLocalPart());
		if (result == 0) {
			return name.getNamespaceURI().compareToIgnoreCase(
					other.name.getNamespaceURI());
		}

		return result;
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return name.getNamespaceURI() + "/" + name.getLocalPart(); //$NON-NLS-1$
	}

	/**
	 * @see Definition#getEntity()
	 */
	@Override
	public Entity getEntity() {
		return new FeatureClass(new About(name.getNamespaceURI(),
				name.getLocalPart()));
	}

	/**
	 * @see Definition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getName().getLocalPart();
	}

	/**
	 * @return the declaringElements
	 */
	public Set<SchemaElement> getDeclaringElements() {
		return declaringElements;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "[type] " + getIdentifier(); //$NON-NLS-1$
	}

	/**
	 * Determine if the attribute type was already set
	 * 
	 * @return if the attribute type was set
	 */
	public boolean isAttributeTypeSet() {
		return type != null;
	}

	/**
	 * Set the attribute type
	 * 
	 * @param type
	 *            the attribute type
	 */
	public void setType(AttributeType type) {
		this.type = type;
	}

	/**
	 * Get the attribute with the given name
	 * 
	 * @param name
	 *            the attribute name
	 * 
	 * @return the attribute definition or <code>null</code>
	 */
	public AttributeDefinition getAttribute(String name) {
		for (AttributeDefinition attrib : getAttributes()) {
			if (attrib.getName().equals(name)) {
				return attrib;
			}
		}

		return null;
	}

}
