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

package eu.esdihumboldt.hale.io.appschema.writer;

import static eu.esdihumboldt.hale.common.align.model.functions.JoinFunction.PARAMETER_JOIN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.DefaultEntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.io.appschema.model.ChainConfiguration;
import eu.esdihumboldt.hale.io.appschema.model.FeatureChaining;
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
	 * GML nil reason type name.
	 */
	public static final String GML_NIL_REASON_TYPE = "NilReasonType";
	/**
	 * GML nil reason attribute name.
	 */
	public static final String GML_NIL_REASON = "nilReason";
	/**
	 * xlink:href qualified name.
	 */
	public static final QName QNAME_XLINK_XREF = new QName("http://www.w3.org/1999/xlink", "href");

	/**
	 * XMLSchema-instance prefix.
	 */
	public static final String XSI_PREFIX = "xsi";
	/**
	 * XMLSchema-instance URI.
	 */
	public static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
	/**
	 * xsi:nil qualified name.
	 */
	public static final QName QNAME_XSI_NIL = new QName(XSI_URI, "nil", XSI_PREFIX);

	/**
	 * HALE INSPIRE extension URI.
	 */
	public static final String HALE_INSPIRE_EXT_URI = "http://www.esdi-humboldt.eu/hale/inspire/ext";

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
	 * Tests whether the provided property definition describes a
	 * <code>nilReason</code> attribute.
	 * 
	 * <p>
	 * Returns {@code true} if the property name is {@code nilReason} and the
	 * property type is either {@code gml:NilReasonType} or the special
	 * <code>&#123;http://www.esdi-humboldt.eu/hale/inspire/ext&#125;NilReasonType</code>
	 * .
	 * </p>
	 * 
	 * @param propertyDef the property definition
	 * @return <code>true</code> if <code>properyDef</code> defines a
	 *         <code>nilReason</code> attribute, <code>false</code> otherwise.
	 */
	public static boolean isNilReason(PropertyDefinition propertyDef) {
		if (propertyDef == null) {
			return false;
		}

		QName propertyName = propertyDef.getName();
		QName propertyTypeName = propertyDef.getPropertyType().getName();

		//
		return (hasGmlNamespace(propertyTypeName)
				|| HALE_INSPIRE_EXT_URI.equals(propertyTypeName.getNamespaceURI()))
				&& GML_NIL_REASON_TYPE.equals(propertyTypeName.getLocalPart())
				&& GML_NIL_REASON.equals(propertyName.getLocalPart());
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
	 * Tests whether the provided property definition describes a nillable XML
	 * element.
	 * 
	 * @param propertyDef the property definition
	 * @return <code>true</code> if <code>properyDef</code> is nillable,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isNillable(PropertyDefinition propertyDef) {
		NillableFlag nillableFlag = propertyDef.getConstraint(NillableFlag.class);

		return nillableFlag != null && nillableFlag.isEnabled();
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
		if (hasGmlNamespace(typeName)
				&& GML_ABSTRACT_FEATURE_TYPE.equals(typeName.getLocalPart())) {
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
		if (hasGmlNamespace(typeName)
				&& GML_ABSTRACT_GEOMETRY_TYPE.equals(typeName.getLocalPart())) {
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
	 * Determines which is the closest type containing the specified property,
	 * among the provided collection of allowed types.
	 * 
	 * <p>
	 * The lookup is done by traversing the property path backwards (i.e. from
	 * end to beginning).
	 * </p>
	 * 
	 * @param propertyEntityDef the property definition
	 * @param allowedTypes the allowed types
	 * @return the type containing the specified property
	 */
	public static TypeDefinition findOwningType(PropertyEntityDefinition propertyEntityDef,
			Collection<? extends TypeDefinition> allowedTypes) {
		List<ChildContext> propertyPath = propertyEntityDef.getPropertyPath();

		return findOwningType(propertyPath, allowedTypes);
	}

	/**
	 * Determines which is the closest type containing the specified property,
	 * among the provided collection of allowed types.
	 * 
	 * <p>
	 * The lookup is done by traversing the property path backwards (i.e. from
	 * end to beginning).
	 * </p>
	 * 
	 * @param propertyPath the property path
	 * @param allowedTypes the allowed types
	 * @return the type containing the specified property
	 */
	public static TypeDefinition findOwningType(List<ChildContext> propertyPath,
			Collection<? extends TypeDefinition> allowedTypes) {
		int ftIdx = findOwningTypeIndex(propertyPath, allowedTypes);

		if (ftIdx >= 0) {
			return propertyPath.get(ftIdx).getChild().getParentType();
		}
		else {
			return null;
		}
	}

	private static int findOwningTypeIndex(List<ChildContext> propertyPath,
			Collection<? extends TypeDefinition> allowedTypes) {
		for (int i = propertyPath.size() - 1; i >= 0; i--) {
			ChildContext childContext = propertyPath.get(i);
			TypeDefinition parentType = childContext.getChild().getParentType();
			if (allowedTypes.contains(parentType)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Determines the path to the closest type containing the specified
	 * property, among the provided collection of allowed types.
	 * 
	 * <p>
	 * The lookup is done by traversing the property path backwards (i.e. from
	 * end to beginning).
	 * </p>
	 * 
	 * @param propertyEntityDef the property definition
	 * @param allowedTypes the allowed types
	 * @return the path to the type containing the specified property
	 */
	public static List<ChildContext> findOwningTypePath(PropertyEntityDefinition propertyEntityDef,
			Collection<? extends TypeDefinition> allowedTypes) {
		List<ChildContext> propertyPath = propertyEntityDef.getPropertyPath();

		return findOwningTypePath(propertyPath, allowedTypes);
	}

	/**
	 * Determines the path to the closest type containing the specified
	 * property, among the provided collection of allowed types.
	 * 
	 * <p>
	 * The lookup is done by traversing the property path backwards (i.e. from
	 * end to beginning).
	 * </p>
	 * 
	 * @param propertyPath the property path
	 * @param allowedTypes the allowed types
	 * @return the path to the type containing the specified property
	 */
	public static List<ChildContext> findOwningTypePath(List<ChildContext> propertyPath,
			Collection<? extends TypeDefinition> allowedTypes) {
		int ftIdx = findOwningTypeIndex(propertyPath, allowedTypes);

		if (ftIdx >= 0) {
			return getContainerPropertyPath(propertyPath.subList(0, ftIdx));
		}

		return Collections.emptyList();
	}

	/**
	 * Looks for one of the allowed types, among the children of the provided
	 * type.
	 * 
	 * <p>
	 * NOTE: if more than one of the allowed types are found, only the first one
	 * is returned.
	 * </p>
	 * 
	 * @param typeDef the type definition
	 * @param allowedTypes the allowed types
	 * @return the first child of <code>typeDef</code> who is a feature type
	 */
	public static TypeDefinition findChildType(TypeDefinition typeDef,
			Collection<? extends TypeDefinition> allowedTypes) {
		if (typeDef != null) {
			Collection<? extends ChildDefinition<?>> children = typeDef.getChildren();
			if (children != null) {
				for (ChildDefinition<?> child : children) {
					PropertyDefinition childPropertyDef = child.asProperty();
					if (childPropertyDef != null) {
						TypeDefinition childPropertyType = childPropertyDef.getPropertyType();
						if (allowedTypes.contains(childPropertyType)) {
							return childPropertyType;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Makes sure the provided property entity is indeed a geometry, and
	 * retrieves its container property entity (i.e. the geometry property, in
	 * GML parlance).
	 * 
	 * @param geometry the geometry entity
	 * @return the geometry property entity
	 */
	public static EntityDefinition getGeometryPropertyEntity(PropertyEntityDefinition geometry) {
		if (!isGeometryType(geometry.getDefinition().getPropertyType())) {
			throw new IllegalArgumentException("Provided entity definition is not a geometry");
		}

		List<ChildContext> geometryPropertyPath = getContainerPropertyPath(
				geometry.getPropertyPath());
		return AlignmentUtil.createEntity(geometry.getType(), geometryPropertyPath,
				geometry.getSchemaSpace(), geometry.getFilter());
	}

	private static List<ChildContext> getContainerPropertyPath(List<ChildContext> propertyPath) {
		if (propertyPath == null || propertyPath.size() == 0) {
			return Collections.emptyList();
		}

		int lastIdx = propertyPath.size() - 1;
		// make sure last element is a property and not a group (e.g. a choice
		// element)
		while (lastIdx > 0 && propertyPath.get(lastIdx - 1).getChild().asProperty() == null) {
			lastIdx--;
		}
		return propertyPath.subList(0, lastIdx);
	}

	/**
	 * Checks whether the property with path <code>nestedPath</code> is actually
	 * nested inside the property with path <code>containerPath</code>.
	 * 
	 * @param containerPath the container property path
	 * @param nestedPath the nested property path
	 * @return <code>true</code> if it is contained, false otherwise
	 */
	public static boolean isNested(List<ChildContext> containerPath,
			List<ChildContext> nestedPath) {
		boolean isContained = true;
		if (containerPath.size() >= nestedPath.size()) {
			isContained = false;
		}
		else {
			for (int i = 0; i < containerPath.size(); i++) {
				if (!containerPath.get(i).equals(nestedPath.get(i))) {
					isContained = false;
					break;
				}
			}
		}

		return isContained;
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

	/**
	 * @param joinCell the join cell
	 * @return the {@link JoinParameter} transformation parameter, or
	 *         <code>null</code> if none is found.
	 */
	public static JoinParameter getJoinParameter(Cell joinCell) {
		if (joinCell != null && joinCell.getTransformationParameters() != null) {
			List<ParameterValue> joinParameterList = joinCell.getTransformationParameters()
					.get(PARAMETER_JOIN);
			if (joinParameterList != null && joinParameterList.size() > 0) {
				return joinParameterList.get(0).as(JoinParameter.class);
			}
		}

		return null;
	}

	/**
	 * @param joinParameter the join parameter
	 * @return the list of join conditions, sorted by join type
	 */
	public static List<JoinCondition> getSortedJoinConditions(final JoinParameter joinParameter) {
		List<JoinCondition> conditions = new ArrayList<JoinCondition>();

		if (joinParameter != null) {
			conditions.addAll(joinParameter.getConditions());
			Collections.sort(conditions, new Comparator<JoinCondition>() {

				@Override
				public int compare(JoinCondition o1, JoinCondition o2) {
					TypeEntityDefinition o1Type = AlignmentUtil.getTypeEntity(o1.joinProperty);
					TypeEntityDefinition o2Type = AlignmentUtil.getTypeEntity(o2.joinProperty);
					return joinParameter.getTypes().indexOf(o1Type)
							- joinParameter.getTypes().indexOf(o2Type);
				}
			});
		}

		return conditions;
	}

	/**
	 * @param cell the cell
	 * @param parameterName the parameter name
	 * @return the value of the specified parameter, or <code>null</code> if it
	 *         is not found
	 */
	public static ParameterValue getTransformationParameter(Cell cell, String parameterName) {
		ListMultimap<String, ParameterValue> parameters = cell.getTransformationParameters();

		if (parameters != null && !parameters.isEmpty() && parameters.get(parameterName) != null
				&& !parameters.get(parameterName).isEmpty()) {
			return parameters.get(parameterName).get(0);
		}
		else {
			return null;
		}
	}

	/**
	 * Converts the given element to a JAXB property type. If any exception
	 * occurs <code>null</code> is returned.
	 * 
	 * @param fragment the fragment to convert
	 * @return the property type or <code>null</code>
	 */
	public static PropertyType propertyTypeFromDOM(Element fragment) {
		try {
			JAXBContext jc = JAXBContext.newInstance(JaxbAlignmentIO.ALIGNMENT_CONTEXT,
					PropertyType.class.getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();

			// it will debug problems while unmarshalling
			u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

			JAXBElement<PropertyType> root = u.unmarshal(fragment, PropertyType.class);

			return root.getValue();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Goes through all chain configurations in the provided feature chaining
	 * configuration and attempts to resolve all unresolved property entity
	 * definitions.
	 * 
	 * <p>
	 * More specifically, resolution for a particular chain configuration is
	 * attempted if {@link ChainConfiguration#getJaxbNestedTypeTarget()} returns
	 * a value, while {@link ChainConfiguration#getNestedTypeTarget()} returns
	 * <code>null</code>.
	 * </p>
	 * 
	 * <p>
	 * Upon successful resolution,
	 * {@link ChainConfiguration#setJaxbNestedTypeTarget(PropertyType)} is
	 * invoked with a <code>null</code> argument, to avoid further entity
	 * resolution attempts.
	 * </p>
	 * 
	 * <p>
	 * A {@link DefaultEntityResolver} instance is used to resolve entities.
	 * </p>
	 * 
	 * @param featureChaining the global feature chaining configuration
	 * @param types the schema to use for entity lookup
	 * @param ssid the schema space identifier
	 */
	public static void resolvePropertyTypes(FeatureChaining featureChaining, TypeIndex types,
			SchemaSpaceID ssid) {
		if (featureChaining != null) {
			EntityResolver resolver = new DefaultEntityResolver();
			for (String joinCellId : featureChaining.getJoins().keySet()) {
				List<ChainConfiguration> chains = featureChaining.getChains(joinCellId);
				for (ChainConfiguration chain : chains) {
					if (chain.getNestedTypeTarget() == null
							&& chain.getJaxbNestedTypeTarget() != null) {
						Property resolved = resolver
								.resolveProperty(chain.getJaxbNestedTypeTarget(), types, ssid);
						if (resolved != null) {
							chain.setNestedTypeTarget(resolved.getDefinition());
							chain.setJaxbNestedTypeTarget(null);
						}
					}
				}
			}
		}
	}
}
