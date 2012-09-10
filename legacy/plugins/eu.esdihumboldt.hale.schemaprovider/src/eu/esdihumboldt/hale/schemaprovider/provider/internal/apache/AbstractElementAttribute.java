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

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaDocumentation;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.NameImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Attribute represented as element in XML
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public abstract class AbstractElementAttribute extends AttributeDefinition {

	private static final Logger log = Logger
			.getLogger(AbstractElementAttribute.class);

	private final boolean nillable;

	private long minOccurs;

	private final long maxOccurs;

	/**
	 * Constructor
	 * 
	 * @param declaringType
	 *            the declaring type, if it is <code>null</code>, the attribute
	 *            type will not be determined
	 * @param name
	 *            the attribute name
	 * @param typeName
	 *            the name of the attribute type
	 * @param element
	 *            the element defining the attribute
	 */
	public AbstractElementAttribute(TypeDefinition declaringType, String name,
			Name typeName, XmlSchemaElement element) {
		super(name, typeName, null, true,
				((element.getSubstitutionGroup() == null) ? (null)
						: (new NameImpl(element.getSubstitutionGroup()
								.getNamespaceURI(), element
								.getSubstitutionGroup().getLocalPart()))));

		nillable = element.isNillable(); // XXX correct?
		minOccurs = element.getMinOccurs(); // XXX correct?
		maxOccurs = element.getMaxOccurs(); // XXX correct?
		setNamespace(getNamespace(element));

		String description = getDescription(element);
		if (description != null) {
			setDescription(description);
		}

		if (declaringType != null) {
			// set the declaring type
			declaringType.addDeclaredAttribute(this);
		}
	}

	private String getNamespace(XmlSchemaElement element) {
		if (element.getQName() != null) {
			return element.getQName().getNamespaceURI();
		} else if (element.getRefName() != null) {
			return element.getRefName().getNamespaceURI();
		} else {
			return null;
		}
	}

	/**
	 * Copy constructor
	 * 
	 * @param other
	 *            the attribute to copy
	 */
	protected AbstractElementAttribute(AbstractElementAttribute other) {
		super(other);

		nillable = other.isNillable();
		minOccurs = other.getMinOccurs();
		maxOccurs = other.getMaxOccurs();
	}

	/**
	 * Check if the given type definition should be set as the attribute type
	 * 
	 * @param typeDef
	 *            the type definition
	 * 
	 * @return the type definition that shall be set as the attribute type
	 */
	protected TypeDefinition checkAttributeType(TypeDefinition typeDef) {
		// inspire geometry attributes
		if (getName().equalsIgnoreCase("geometry") && typeDef != null && //$NON-NLS-1$
				!Geometry.class.isAssignableFrom(typeDef.getType(null)
						.getBinding())) {
			return createDefaultGeometryType(typeDef);
		}
		// geometry property types
		// else if (typeDef != null &&
		// typeDef.getName().getLocalPart().equals("GeometryPropertyType") &&
		// !Geometry.class.isAssignableFrom(typeDef.getType(null).getBinding()))
		// {
		// return createDefaultGeometryType(typeDef);
		// }
		// geometric primitive property types
		else if (typeDef != null
				&& typeDef.getName().getLocalPart()
						.equals("GeometricPrimitivePropertyType") && //$NON-NLS-1$
				!Geometry.class.isAssignableFrom(typeDef.getType(null)
						.getBinding())) {
			return createDefaultGeometryType(typeDef);
		}

		// default: leave type untouched
		return typeDef;
	}

	private TypeDefinition createDefaultGeometryType(TypeDefinition typeDef) {
		// create an attribute type with a geometry binding
		AttributeType attributeType = createDefaultGeometryAttributeType(getTypeName());

		TypeDefinition result = new TypeDefinition(getTypeName(),
				attributeType, typeDef.getSuperType());
		result.setDescription(typeDef.getDescription());
		result.setLocation(typeDef.getLocation());
		return result;
	}

	/**
	 * Create a default attribute type with a geometry binding
	 * 
	 * @param name
	 *            the type name
	 * 
	 * @return the attribute type
	 */
	public static AttributeType createDefaultGeometryAttributeType(Name name) {
		AttributeTypeBuilder builder = new AttributeTypeBuilder();
		builder.setBinding(Geometry.class);
		builder.setName(name.getLocalPart());
		builder.setNamespaceURI(name.getNamespaceURI());
		builder.setNillable(true);
		return builder.buildType();
	}

	/**
	 * Get the documentation from an annotated XML object
	 * 
	 * @param element
	 *            the annotated element
	 * @return the description or <code>null</code>
	 */
	public static String getDescription(XmlSchemaAnnotated element) {
		if (element.getAnnotation() != null) {
			XmlSchemaObjectCollection annotationItems = element.getAnnotation()
					.getItems();
			StringBuffer desc = new StringBuffer();
			for (int i = 0; i < annotationItems.getCount(); i++) {
				XmlSchemaObject item = annotationItems.getItem(i);
				if (item instanceof XmlSchemaDocumentation) {
					XmlSchemaDocumentation doc = (XmlSchemaDocumentation) item;
					NodeList markup = doc.getMarkup();
					for (int j = 0; j < markup.getLength(); j++) {
						Node node = markup.item(j);
						desc.append(node.getTextContent());
						desc.append('\n');
					}
				}
			}

			String description = desc.toString();
			if (!description.isEmpty()) {
				return description;
			}
		}

		return null;
	}

	/**
	 * @see AttributeDefinition#createAttributeDescriptor(Set)
	 */
	@Override
	public AttributeDescriptor createAttributeDescriptor(
			Set<TypeDefinition> resolving) {
		TypeDefinition attType = getAttributeType();
		if (attType != null) {
			if (resolving != null && resolving.contains(attType)) {
				log.warn("Cycle detected, skipping creation of attribute descriptor " //$NON-NLS-1$
						+ getName()
						+ ":" + attType.getDisplayName() + " in " + getDeclaringType().getDisplayName()); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			} else {
				AttributeType type = attType.getType(resolving);
				if (type != null) {
					// Name parentName = getDeclaringType().getName();
					return new AttributeDescriptorImpl(type, new NameImpl(null,
							getName()), // must be null namespace because
										// otherwise the StreamingRenderer and
										// some other Geotools components choke
										// on it
							(int) minOccurs, (int) maxOccurs, true, // always
																	// nillable,
																	// else
																	// creating
																	// the
																	// features
																	// fails
							null);
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * @see AttributeDefinition#getMaxOccurs()
	 */
	@Override
	public long getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @see AttributeDefinition#getMinOccurs()
	 */
	@Override
	public long getMinOccurs() {
		return minOccurs;
	}

	/**
	 * @see AttributeDefinition#isNillable()
	 */
	@Override
	public boolean isNillable() {
		return nillable;
	}

	/**
	 * @param minOccurs
	 *            the minOccurs to set
	 */
	public void setMinOccurs(long minOccurs) {
		this.minOccurs = minOccurs;
	}

}
