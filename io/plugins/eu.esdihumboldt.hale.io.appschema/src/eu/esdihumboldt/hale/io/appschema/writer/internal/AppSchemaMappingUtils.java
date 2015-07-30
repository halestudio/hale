/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.appschema.writer.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Utility method for app-schema mapping generation.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaMappingUtils {

	/**
	 * Base GML namespace, common to all GML versions.
	 */
	public static final String GML_BASE_NAMESPACE = "http://www.opengis.net/gml";
	/**
	 * GML identifier tag name.
	 */
	public static final String GML_ID = "id";
	/**
	 * GML abstract feature type name.
	 */
	public static final String GML_ABSTRACT_FEATURE_TYPE = "AbstractFeatureType";
	/**
	 * GML abstract geometry type name.
	 */
	public static final String GML_ABSTRACT_GEOMETRY_TYPE = "AbstractGeometryType";
	/**
	 * xlink:href qualified name.
	 */
	public static final QName QNAME_XLINK_XREF = new QName("http://www.w3.org/1999/xlink", "href");

	/**
	 * Tests whether the provided property definition describes a
	 * <code>gml:id</code> attribute.
	 * 
	 * @param propertyDef the property definition
	 * @return <code>true</code> if <code>properyDef</code> defines a
	 *         <code>gml:id</code> attribute, <code>false</code> otherwise.
	 */
	public static boolean isGmlId(PropertyDefinition propertyDef) {
		if (propertyDef == null) {
			return false;
		}

		QName propertyName = propertyDef.getName();

		return hasGmlNamespace(propertyName) && GML_ID.equals(propertyName.getLocalPart());
	}

	/**
	 * Tests whether the provided property definition describes an XML
	 * attribute.
	 * 
	 * @param propertyDef the property definition
	 * @return <code>true</code> if <code>properyDef</code> defines an XML
	 *         attribute, <code>false</code> otherwise.
	 */
	public static boolean isXmlAttribute(PropertyDefinition propertyDef) {
		XmlAttributeFlag xmlAttrFlag = propertyDef.getConstraint(XmlAttributeFlag.class);

		return xmlAttrFlag != null && xmlAttrFlag.isEnabled();
	}

	/**
	 * Tests whether the provided type definition describes a GML feature type.
	 * 
	 * @param typeDefinition the type definition
	 * @return <code>true</code> if <code>typeDefinition</code> defines a GML
	 *         feature type, <code>false</code> otherwise.
	 */
	public static boolean isFeatureType(TypeDefinition typeDefinition) {
		if (typeDefinition == null) {
			return false;
		}

		QName typeName = typeDefinition.getName();
		if (hasGmlNamespace(typeName) && GML_ABSTRACT_FEATURE_TYPE.equals(typeName.getLocalPart())) {
			return true;
		}
		else {
			return isFeatureType(typeDefinition.getSuperType());
		}
	}

	/**
	 * Tests whether the provided type definition describes a GML geometry type.
	 * 
	 * @param typeDefinition the type definition
	 * @return <code>true</code> if <code>typeDefinition</code> defines a GML
	 *         geometry type, <code>false</code> otherwise.
	 */
	public static boolean isGeometryType(TypeDefinition typeDefinition) {
		if (typeDefinition == null) {
			return false;
		}

		QName typeName = typeDefinition.getName();
		if (hasGmlNamespace(typeName) && GML_ABSTRACT_GEOMETRY_TYPE.equals(typeName.getLocalPart())) {
			return true;
		}
		else {
			return isGeometryType(typeDefinition.getSuperType());
		}
	}

	private static boolean hasGmlNamespace(QName qname) {
		return qname.getNamespaceURI().startsWith(GML_BASE_NAMESPACE);
	}

	/**
	 * Tests whether the provided property definition describes a multi-valued
	 * property.
	 * 
	 * @param targetPropertyDef the property definition
	 * @return <code>true</code> if <code>targetPropertyDef</code> defines a
	 *         multi-valued property, <code>false</code> otherwise.
	 */
	public static boolean isMultiple(PropertyDefinition targetPropertyDef) {
		if (targetPropertyDef != null) {
			Cardinality cardinality = targetPropertyDef.getConstraint(Cardinality.class);
			if (cardinality != null) {
				long maxOccurs = cardinality.getMaxOccurs();
				if (maxOccurs > 1 || maxOccurs == Cardinality.UNBOUNDED) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Tests whether the provided property definition describes an HREF
	 * attribute.
	 * 
	 * @param propertyDef the property definition
	 * @return <code>true</code> if <code>targetPropertyDef</code> defines an
	 *         HREF attribute, <code>false</code> otherwise.
	 */
	public static boolean isHRefAttribute(PropertyDefinition propertyDef) {
		return propertyDef != null && propertyDef.getName().equals(QNAME_XLINK_XREF);
	}

	/**
	 * Determines the closest feature type containing the provided property.
	 * 
	 * <p>
	 * The lookup is done by traversing the property path backwards (i.e. from
	 * end to beginning).
	 * </p>
	 * 
	 * @param propertyEntityDef the property definition
	 * @return the feature type containing the provided property
	 */
	public static TypeDefinition findOwningFeatureType(PropertyEntityDefinition propertyEntityDef) {
		List<ChildContext> propertyPath = propertyEntityDef.getPropertyPath();

		return findOwningFeatureType(propertyPath);
	}

	/**
	 * Determines the closest feature type containing the provided property.
	 * 
	 * <p>
	 * The lookup is done by traversing the property path backwards (i.e. from
	 * end to beginning).
	 * </p>
	 * 
	 * @param propertyPath the property path
	 * @return the feature type containing the provided property
	 */
	public static TypeDefinition findOwningFeatureType(List<ChildContext> propertyPath) {
		int ftIdx = findOwningFeatureTypeIndex(propertyPath);

		if (ftIdx >= 0) {
			return propertyPath.get(ftIdx).getChild().getParentType();
		}
		else {
			return null;
		}
	}

	/**
	 * Determines the path to the closest feature type containing the provided
	 * property.
	 * 
	 * <p>
	 * The lookup is done by traversing the property path backwards (i.e. from
	 * end to beginning).
	 * </p>
	 * 
	 * @param propertyEntityDef the property definition
	 * @return the path to the feature type containing the provided property
	 */
	public static List<ChildContext> findOwningFeatureTypePath(
			PropertyEntityDefinition propertyEntityDef) {
		List<ChildContext> propertyPath = propertyEntityDef.getPropertyPath();

		return findOwningFeatureTypePath(propertyPath);
	}

	/**
	 * Determines the path to the closest feature type containing the provided
	 * property.
	 * 
	 * <p>
	 * The lookup is done by traversing the property path backwards (i.e. from
	 * end to beginning).
	 * </p>
	 * 
	 * @param propertyPath the property path
	 * @return the path to the feature type containing the provided property
	 */
	public static List<ChildContext> findOwningFeatureTypePath(List<ChildContext> propertyPath) {
		int ftIdx = findOwningFeatureTypeIndex(propertyPath);

		if (ftIdx >= 0) {
			return getContainerPropertyPath(propertyPath.subList(0, ftIdx));
		}

		return Collections.emptyList();
	}

	private static int findOwningFeatureTypeIndex(List<ChildContext> propertyPath) {
		for (int i = propertyPath.size() - 1; i >= 0; i--) {
			ChildContext childContext = propertyPath.get(i);
			TypeDefinition parentType = childContext.getChild().getParentType();
			if (isFeatureType(parentType)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Looks for a feature type among the children of the provided type.
	 * 
	 * <p>
	 * NOTE: if more than one children are feature types, only the first one is
	 * returned.
	 * </p>
	 * 
	 * @param typeDef the type definition
	 * @return the first child of <code>typeDef</code> who is a feature type
	 */
	public static TypeDefinition findChildFeatureType(TypeDefinition typeDef) {
		if (typeDef != null) {
			Collection<? extends ChildDefinition<?>> children = typeDef.getChildren();
			if (children != null) {
				for (ChildDefinition<?> child : children) {
					PropertyDefinition childPropertyDef = child.asProperty();
					if (childPropertyDef != null) {
						TypeDefinition childPropertyType = childPropertyDef.getPropertyType();
						if (isFeatureType(childPropertyType)) {
							return childPropertyType;
						}
					}
				}
			}
		}

		return null;
	}

	private static List<ChildContext> getContainerPropertyPath(List<ChildContext> propertyPath) {
		if (propertyPath == null || propertyPath.size() == 0) {
			return Collections.emptyList();
		}

		int lastIdx = propertyPath.size() - 1;
		// make sure last element is a property and not a group (e.g. a choice
		// element)
		while (lastIdx > 0 && propertyPath.get(lastIdx).getChild().asProperty() == null) {
			lastIdx--;
		}
		return propertyPath.subList(0, lastIdx);
	}

	/**
	 * Return the first target {@link Entity}, which is assumed to be a
	 * {@link Property}.
	 * 
	 * @param propertyCell the property cell
	 * @return the target {@link Property}
	 */
	public static Property getTargetProperty(Cell propertyCell) {
		ListMultimap<String, ? extends Entity> targetEntities = propertyCell.getTarget();
		if (targetEntities != null && !targetEntities.isEmpty()) {
			return (Property) targetEntities.values().iterator().next();
		}

		return null;
	}

	/**
	 * Return the first source {@link Entity}, which is assumed to be a
	 * {@link Property}.
	 * 
	 * @param propertyCell the property cell
	 * @return the target {@link Property}
	 */
	public static Property getSourceProperty(Cell propertyCell) {
		ListMultimap<String, ? extends Entity> sourceEntities = propertyCell.getSource();
		if (sourceEntities != null && !sourceEntities.isEmpty()) {
			return (Property) sourceEntities.values().iterator().next();
		}

		return null;
	}

	/**
	 * Return the first target {@link Entity}, which is assumed to be a
	 * {@link Type}.
	 * 
	 * @param typeCell the type cell
	 * @return the target {@link Type}
	 */
	public static Type getTargetType(Cell typeCell) {
		ListMultimap<String, ? extends Entity> targetEntities = typeCell.getTarget();
		if (targetEntities != null && !targetEntities.isEmpty()) {
			return (Type) targetEntities.values().iterator().next();
		}

		return null;
	}

	/**
	 * Convert the provided value to a CQL literal, based on the property
	 * definition.
	 * 
	 * <p>
	 * In practice, this means that for properties whose binding type is a
	 * {@link Number}, the value is returned as is; otherwise, value is
	 * translated to a string literal and wrapped in single quotes.
	 * </p>
	 * 
	 * @param propertyDef the property definition
	 * @param value the value to convert
	 * @return the value as CQL literal
	 */
	public static String asCqlLiteral(PropertyDefinition propertyDef, String value) {
		if (propertyDef != null && value != null) {
			TypeDefinition typeDef = propertyDef.getPropertyType();
			HasValueFlag hasValue = typeDef.getConstraint(HasValueFlag.class);
			if (hasValue != null && hasValue.equals(HasValueFlag.get(true))) {
				Binding binding = typeDef.getConstraint(Binding.class);
				if (binding != null && Number.class.isAssignableFrom(binding.getBinding())) {
					return value;
				}
				else {
					// treat value as a string literal and hope for the best
					return "'" + value + "'";
				}
			}
		}

		return value;
	}

	/**
	 * Test whether the cell represents a Join transformation.
	 * 
	 * @param typeCell the cell to test
	 * @return true if the cell represents a Join transformation, false
	 *         otherwise
	 */
	public static boolean isJoin(Cell typeCell) {
		return typeCell != null && JoinFunction.ID.equals(typeCell.getTransformationIdentifier());
	}
}
