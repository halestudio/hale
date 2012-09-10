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

package eu.esdihumboldt.hale.schemaprovider.provider.internal.apache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeUnion;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.NameImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.gml3.GMLSchema;
import org.geotools.xs.XSSchema;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import de.cs3d.util.logging.AGroup;
import de.cs3d.util.logging.AGroupFactory;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.schemaprovider.EnumAttributeType;
import eu.esdihumboldt.hale.schemaprovider.EnumAttributeTypeImpl;
import eu.esdihumboldt.hale.schemaprovider.Messages;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public abstract class TypeUtil {

	private static final ALogger log = ALoggerFactory.getLogger(TypeUtil.class);

	private static final AGroup TYPE_RESOLVE = AGroupFactory.getGroup(Messages
			.getString("TypeUtil.0")); //$NON-NLS-1$

	/**
	 * The XS schema
	 */
	protected static final XSSchema xsSchema = new XSSchema();

	/**
	 * The GML schema
	 */
	protected static final GMLSchema gml3Schema = new GMLSchema();

	/**
	 * Geotools bindings location string
	 */
	private static final String GEOTOOLS_LOC = Messages.getString("TypeUtil.1"); //$NON-NLS-1$

	/**
	 * Geotools bindings location prefix
	 */
	private static final String GEOTOOLS_LOC_PREFIX = Messages
			.getString("TypeUtil.2"); //$NON-NLS-1$

	/**
	 * GML 3.2 namespace
	 */
	private static final String NAMESPACE_GML3_2 = "http://www.opengis.net/gml/3.2"; //$NON-NLS-1$

	private static final String NAMESPACE_GML = "http://www.opengis.net/gml"; //$NON-NLS-1$

	/**
	 * Set of XML schema types that should get a String binding but don't get
	 * one through the Geotools bindings
	 * 
	 * @see "http://www.w3schools.com/Schema/schema_dtypes_string.asp"
	 */
	private static final Set<String> XS_STRING_TYPES = new HashSet<String>();
	static {
		XS_STRING_TYPES.add("ID"); //$NON-NLS-1$
		XS_STRING_TYPES.add("IDREF"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NCName"); //$NON-NLS-1$
		XS_STRING_TYPES.add("token"); //$NON-NLS-1$
		XS_STRING_TYPES.add("Name"); //$NON-NLS-1$
		XS_STRING_TYPES.add("language"); //$NON-NLS-1$
		XS_STRING_TYPES.add("ENTITY"); //$NON-NLS-1$
		XS_STRING_TYPES.add("ENTITIES"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NMTOKEN"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NMTOKENS"); //$NON-NLS-1$
		XS_STRING_TYPES.add("normalizedString"); //$NON-NLS-1$
		XS_STRING_TYPES.add("QName"); //$NON-NLS-1$
	}

	/**
	 * Resolve an attribute type
	 * 
	 * @param typeName
	 *            the type name
	 * @param schemaTypes
	 *            the schema type resolver
	 * @return the type definition or <code>null</code>
	 */
	public static TypeDefinition resolveAttributeType(Name typeName,
			SchemaTypeResolver schemaTypes) {
		TypeDefinition typeDef = getXSType(typeName);

		// Try to resolve the attribute bindings

		if (typeDef == null) {
			typeDef = getSchemaType(typeName, schemaTypes);
		}

		if (typeDef == null) {
			log.warn(
					TYPE_RESOLVE,
					"Type could not be resolved: " + typeName.getNamespaceURI() + "/" + typeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return typeDef;
	}

	/**
	 * Resolve an element type
	 * 
	 * @param element
	 *            the element
	 * @param typeName
	 *            the type name
	 * @param schemaTypes
	 *            the schema types
	 * @return the type definition or <code>null</code>
	 */
	public static TypeDefinition resolveElementType(XmlSchemaElement element,
			Name typeName, SchemaTypeResolver schemaTypes) {
		TypeDefinition typeDef = getXSType(typeName);

		// Try to resolve the attribute bindings

		if (typeDef == null) {
			typeDef = getSchemaType(typeName, schemaTypes);
		}

		if (typeDef == null) {
			// Bindings for simple types
			typeDef = getSimpleAttributeType(element, typeName, schemaTypes);
		}

		if (typeDef == null) {
			log.warn(
					TYPE_RESOLVE,
					"Type could not be resolved: " + typeName.getNamespaceURI() + "/" + typeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return typeDef;
	}

	/**
	 * Get the XML schema type
	 * 
	 * @param name
	 *            the type name
	 * 
	 * @return the type definition or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	private static TypeDefinition getXSType(Name name) {
		AttributeType ty = xsSchema.get(name);

		// special case: ID etc. - assure String binding
		if (ty != null && XS_STRING_TYPES.contains(name.getLocalPart())) {
			ty = new AttributeTypeImpl(name, java.lang.String.class, false,
					false, Collections.EMPTY_LIST, XSSchema.NCNAME_TYPE, null);
		}

		if (ty != null) {
			TypeDefinition typeDef = new TypeDefinition(name, ty, null);
			typeDef.setLocation(Messages.getString("TypeUtil.10")); //$NON-NLS-1$
			return typeDef;
		} else {
			return null;
		}
	}

	/**
	 * Get the type from the schema
	 * 
	 * @param typeName
	 *            the type name
	 * @param schemaTypes
	 *            the schema types
	 * 
	 * @return the schema type or <code>null</code>
	 */
	private static TypeDefinition getSchemaType(Name typeName,
			SchemaTypeResolver schemaTypes) {
		if (schemaTypes != null) {
			TypeDefinition schemaType = schemaTypes.getSchemaType(typeName);

			// GML bindings
			AttributeType gmlType = getGMLAttributeType(typeName);
			if (gmlType != null) {
				if (schemaType != null) {
					AttributeType t = schemaType.getType(null);
					if (t == null || t.getBinding().equals(Collection.class)) {
						// only replace type if we don't already have a good
						// binding
						schemaType.setType(gmlType); // replace the internal
														// type with the
														// geotools binding

						// update location
						if (schemaType.getLocation() == null) {
							schemaType.setLocation(GEOTOOLS_LOC);
						} else if (!schemaType.getLocation().startsWith(
								GEOTOOLS_LOC_PREFIX)) {
							schemaType.setLocation(GEOTOOLS_LOC_PREFIX
									+ schemaType.getLocation());
						}
					}
				} else {
					schemaType = new TypeDefinition(typeName, gmlType, null);
					schemaType.setLocation(GEOTOOLS_LOC);
				}
			}

			return schemaType;
		} else {
			return null;
		}
	}

	/**
	 * Get the attribute type for an GML type
	 * 
	 * @param typeName
	 *            the type name
	 * 
	 * @return the attribute type or <code>null</code>
	 */
	public static AttributeType getGMLAttributeType(Name typeName) {
		AttributeType gmlType = gml3Schema.get(typeName);
		if (gmlType == null
				&& typeName.getNamespaceURI().equals(NAMESPACE_GML3_2)) {
			// try again with GML2/3 namespace
			gmlType = gml3Schema.get(new NameImpl(NAMESPACE_GML, typeName
					.getLocalPart()));
			// FIXME replicate type with correct namespace?
		}
		return gmlType;
	}

	/**
	 * Get the predefined attribute type (GML or XS) with the given type name
	 * 
	 * @param typeName
	 *            the type name
	 * 
	 * @return the attribute type or <code>null</code>
	 */
	public static AttributeType getPredefinedAttributeType(Name typeName) {
		AttributeType result = xsSchema.get(typeName);

		if (result == null) {
			result = getGMLAttributeType(typeName);
		}

		return result;
	}

	/**
	 * Returns the attribute type for an enumeration.
	 * 
	 * @param element
	 *            the defining element
	 * @param typeName
	 *            the type name
	 * @param schemaTypes
	 *            the schema types
	 * 
	 * @return the attribute type or <code>null</code>
	 */
	private static TypeDefinition getSimpleAttributeType(
			XmlSchemaElement element, Name typeName,
			SchemaTypeResolver schemaTypes) {
		if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
			return resolveSimpleType(typeName, null,
					(XmlSchemaSimpleType) element.getSchemaType(), schemaTypes);
		} else {
			return null;
		}
	}

	/**
	 * Returns the attribute type for an enumeration.
	 * 
	 * @param simpleTypeRestriction
	 *            the simple type
	 * @param name
	 *            the custom type name or <code>null</code>
	 * @param schemaTypes
	 *            the schema types
	 * 
	 * @return the attribute type or <code>null</code>
	 */
	public static TypeDefinition getEnumAttributeType(
			XmlSchemaSimpleTypeRestriction simpleTypeRestriction, Name name,
			SchemaTypeResolver schemaTypes) {
		AttributeType type = null;

		Name baseTypeName = new NameImpl(simpleTypeRestriction
				.getBaseTypeName().getNamespaceURI(), simpleTypeRestriction
				.getBaseTypeName().getLocalPart());

		// resolve type
		TypeDefinition baseTypeDef = resolveAttributeType(baseTypeName,
				schemaTypes);
		if (baseTypeDef != null) {
			type = baseTypeDef.getType(null);

			List<String> values = new ArrayList<String>();
			XmlSchemaObjectCollection facets = simpleTypeRestriction
					.getFacets();
			for (int i = 0; i < facets.getCount(); i++) {
				XmlSchemaObject facet = facets.getItem(i);
				if (facet instanceof XmlSchemaEnumerationFacet) {
					String value = ((XmlSchemaEnumerationFacet) facet)
							.getValue().toString();
					values.add(value);
				} else if (facet instanceof XmlSchemaPatternFacet) {
					// TODO support for patterns
				}
				// TODO support for other facets?
			}

			if (!values.isEmpty()) {
				type = new EnumAttributeTypeImpl(type, values, false, name);
			}
		} else {
			log.warn(
					TYPE_RESOLVE,
					"Could not resolve base type: " + baseTypeName.getNamespaceURI() + "/" + baseTypeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (type != null) {
			TypeDefinition typeDef = new TypeDefinition(name, type, null);
			if (schemaTypes != null) {
				typeDef.setLocation(schemaTypes.getSchemaLocation());
			}
			return typeDef;
		} else {
			return null;
		}
	}

	/**
	 * Resolve a simple type
	 * 
	 * @param typeName
	 *            the type name
	 * @param simpleType
	 *            the simple type
	 * @param schemaTypes
	 *            the schema types
	 * 
	 * @return the type definition or <code>null</code> if it couldn't be
	 *         resolved
	 */
	public static TypeDefinition resolveSimpleType(Name typeName,
			XmlSchemaSimpleType simpleType, SchemaTypeResolver schemaTypes) {
		return resolveSimpleType(typeName, null, simpleType, schemaTypes);
	}

	/**
	 * Resolve simple type dependencies
	 * 
	 * @param typeName
	 *            the type name
	 * @param simpleType
	 *            the simple type
	 * 
	 * @return the type definition or <code>null</code> if it couldn't be
	 *         resolved
	 */
	public static Set<Name> getSimpleTypeDependencies(Name typeName,
			XmlSchemaSimpleType simpleType) {
		Set<Name> dependencies = new LinkedHashSet<Name>();
		resolveSimpleType(typeName, dependencies, simpleType, null);
		return dependencies;
	}

	/**
	 * Resolve a simple type
	 * 
	 * @param typeName
	 *            the type name
	 * @param dependencies
	 *            the list to add the dependency names to. if this parameter is
	 *            not null, no type definition must be returned
	 * @param simpleType
	 *            the simple type
	 * @param schemaTypes
	 *            the schema types
	 * 
	 * @return the type definition or <code>null</code> if it couldn't be
	 *         resolved
	 */
	private static TypeDefinition resolveSimpleType(Name typeName,
			Collection<Name> dependencies, XmlSchemaSimpleType simpleType,
			SchemaTypeResolver schemaTypes) {
		TypeDefinition typeDef = null;

		XmlSchemaSimpleTypeContent content = simpleType.getContent();

		if (content instanceof XmlSchemaSimpleTypeUnion) {
			XmlSchemaSimpleTypeUnion union = (XmlSchemaSimpleTypeUnion) content;

			AttributeType attributeType = createUnionAttributeType(typeName,
					dependencies, union, schemaTypes);

			typeDef = new TypeDefinition(typeName, attributeType, null);
		} else if (content instanceof XmlSchemaSimpleTypeList) {
			XmlSchemaSimpleTypeList list = (XmlSchemaSimpleTypeList) content;

			AttributeType attributeType = createListAttributeType(typeName,
					dependencies, list, schemaTypes);

			typeDef = new TypeDefinition(typeName, attributeType, null);
		} else if (content instanceof XmlSchemaSimpleTypeRestriction) {
			typeDef = getEnumAttributeType(
					(XmlSchemaSimpleTypeRestriction) content, typeName,
					schemaTypes);
		} else {
			log.warn(
					TYPE_RESOLVE,
					"Unrecognized simple type " + typeName.getNamespaceURI() + "/" + typeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (typeDef != null && schemaTypes != null) {
			typeDef.setLocation(schemaTypes.getSchemaLocation());
		}

		return typeDef;
	}

	private static AttributeType createUnionAttributeType(Name typeName,
			Collection<Name> dependencies, XmlSchemaSimpleTypeUnion union,
			SchemaTypeResolver schemaTypes) {
		XmlSchemaObjectCollection baseTypes = union.getBaseTypes();
		Class<?> binding = null;
		boolean restrictToValues = true;
		Set<String> allowedValues = new HashSet<String>();

		// base type definitions
		if (baseTypes != null && baseTypes.getCount() > 0) {
			for (int i = 0; i < baseTypes.getCount(); i++) {
				XmlSchemaObject baseType = baseTypes.getItem(i);
				if (baseType instanceof XmlSchemaSimpleType) {
					XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) baseType;
					Name baseName;
					if (simpleType.getQName() != null) {
						baseName = new NameImpl(simpleType.getQName()
								.getNamespaceURI(), simpleType.getQName()
								.getLocalPart());
					} else {
						// anonymous type
						baseName = new NameImpl(
								typeName.getNamespaceURI()
										+ "/" + typeName.getLocalPart(), "AnonymousType" + i); //$NON-NLS-1$ //$NON-NLS-2$
					}
					TypeDefinition baseDef = resolveSimpleType(baseName,
							dependencies, simpleType, schemaTypes);

					if (dependencies == null) {
						if (baseDef != null) {
							AttributeType type = baseDef.getType(null);
							if (binding == null) {
								binding = type.getBinding();
							} else {
								binding = findCompatibleClass(binding,
										type.getBinding());
							}

							// combine values for enums/restrictions
							if (type instanceof EnumAttributeType) {
								EnumAttributeType enumType = (EnumAttributeType) type;
								allowedValues.addAll(enumType
										.getAllowedValues());
								if (enumType.otherValuesAllowed()) {
									restrictToValues = true;
								}
							} else {
								restrictToValues = false;
							}
						} else {
							log.warn(
									TYPE_RESOLVE,
									"Error resolving base type " + baseName.getURI()); //$NON-NLS-1$
						}
					}
				} else {
					log.warn("Unrecognized base type"); //$NON-NLS-1$
				}
			}
		}

		// references base types by name
		QName[] members = union.getMemberTypesQNames();
		if (members != null) {
			for (QName name : members) {
				if (name != null) {
					Name baseName = new NameImpl(name.getNamespaceURI(),
							name.getLocalPart());

					if (dependencies != null) {
						dependencies.add(baseName);
					} else {
						TypeDefinition nameDef = resolveAttributeType(baseName,
								schemaTypes);

						if (nameDef != null) {
							AttributeType type = nameDef.getType(null);
							if (binding == null) {
								binding = type.getBinding();
							} else {
								binding = findCompatibleClass(binding,
										type.getBinding());
							}

							// combine values for enums/restrictions
							if (type instanceof EnumAttributeType) {
								EnumAttributeType enumType = (EnumAttributeType) type;
								allowedValues.addAll(enumType
										.getAllowedValues());
								if (enumType.otherValuesAllowed()) {
									restrictToValues = true;
								}
							} else {
								restrictToValues = false;
							}
						} else {
							log.warn(
									TYPE_RESOLVE,
									"Error resolving base type " + baseName.getURI()); //$NON-NLS-1$
						}
					}
				}
			}
		}

		AttributeType result;
		if (dependencies == null) {
			AttributeTypeBuilder typeBuilder = new AttributeTypeBuilder();
			typeBuilder.setBinding(binding);
			typeBuilder.setName(typeName.getLocalPart());
			typeBuilder.setNamespaceURI(typeName.getNamespaceURI());
			typeBuilder.setNillable(true);
			result = typeBuilder.buildType();

			if (!allowedValues.isEmpty()) {
				result = new EnumAttributeTypeImpl(result, allowedValues,
						!restrictToValues, typeName);
			}
		} else {
			result = null;
		}

		return result;
	}

	private static Class<?> findCompatibleClass(Class<?> binding,
			Class<?> binding2) {
		if (binding == null || binding2 == null) {
			return Object.class;
		}

		if (binding.equals(binding2)) {
			return binding;
		} else if (binding.isAssignableFrom(binding2)) {
			return binding;
		} else if (binding2.isAssignableFrom(binding)) {
			return binding2;
		}
		// special treatment for string - if any binding is compatible to
		// String, it is returned
		else if (String.class.isAssignableFrom(binding)
				|| String.class.isAssignableFrom(binding2)) {
			return String.class;
		} else {
			return findCompatibleClass(binding.getSuperclass(),
					binding2.getSuperclass());
		}
	}

	private static AttributeType createListAttributeType(Name typeName,
			Collection<Name> dependencies, XmlSchemaSimpleTypeList list,
			SchemaTypeResolver schemaTypes) {
		// TODO use item type information
		/*
		 * if (list.getItemType() == null) {
		 * 
		 * } else if (list.getItemTypeName() != null) {
		 * 
		 * }
		 */

		AttributeTypeBuilder typeBuilder = new AttributeTypeBuilder();
		typeBuilder.setBinding(List.class);
		typeBuilder.setName(typeName.getLocalPart());
		typeBuilder.setNamespaceURI(typeName.getNamespaceURI());
		typeBuilder.setNillable(true);
		return typeBuilder.buildType();
	}

}
