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

package eu.esdihumboldt.cst.functions.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Geometry;
import org.springframework.core.convert.ConversionException;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceFactory;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceFactory;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import net.jcip.annotations.Immutable;

/**
 * Property rename function.
 * 
 * @author Simon Templer
 */
@Immutable
public class Rename extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements RenameFunction {

	// object symbolizing that no match for the source to the target definition
	// was found (in contrast to null value)
	private static enum Result {
		NO_MATCH
	}

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException {
		// get the source value
		Object sourceValue = variables.values().iterator().next().getValue();

		boolean structuralRenameEnabled = getOptionalParameter(PARAMETER_STRUCTURAL_RENAME,
				Value.of(false)).as(Boolean.class);

		boolean ignoreNamespacesEnabled = getOptionalParameter(PARAMETER_IGNORE_NAMESPACES,
				Value.of(false)).as(Boolean.class);

		boolean copyGeometriesEnabled = getOptionalParameter(PARAMETER_COPY_GEOMETRIES,
				Value.of(true)).as(Boolean.class);

		// not a group? just return value.
		if (!(sourceValue instanceof Group))
			return sourceValue;
		else if (!structuralRenameEnabled) {
			// not structural rename -> value only
			if (sourceValue instanceof Instance)
				return ((Instance) sourceValue).getValue();
			else
				return null;
		}
		else {
			// structural rename
			Object result = structuralRename(sourceValue, resultProperty.getDefinition(),
					ignoreNamespacesEnabled, new DefaultInstanceFactory(), copyGeometriesEnabled);
			if (result == Result.NO_MATCH)
				return null; // source could neither be used for target value,
								// nor any child properties
			return result;
		}
	}

	/**
	 * Performs a structural rename on the given source object to the given
	 * target definition.
	 * 
	 * @param source the source value (or group/instance)
	 * @param targetDefinition the target definition
	 * @param allowIgnoreNamespaces if for the structure comparison, namespaces
	 *            may be ignored
	 * @param instanceFactory the instance factory
	 * @param copyGeometries specifies if geometry objects should be copied
	 * @return the transformed value (or group/instance) or NO_MATCH
	 */
	public static Object structuralRename(Object source, ChildDefinition<?> targetDefinition,
			boolean allowIgnoreNamespaces, InstanceFactory instanceFactory,
			boolean copyGeometries) {
		return structuralRename(source, targetDefinition, allowIgnoreNamespaces, instanceFactory,
				copyGeometries, null);
	}

	/**
	 * Performs a structural rename on the given source object to the given
	 * target definition.
	 * 
	 * @param source the source value (or group/instance)
	 * @param targetDefinition the target definition
	 * @param allowIgnoreNamespaces if for the structure comparison, namespaces
	 *            may be ignored
	 * @param instanceFactory the instance factory
	 * @param copyGeometries specifies if geometry objects should be copied
	 * @param skipChildren a set of direct children to skip or <code>null</code>
	 * @return the transformed value (or group/instance) or NO_MATCH
	 */
	public static Object structuralRename(Object source, ChildDefinition<?> targetDefinition,
			boolean allowIgnoreNamespaces, InstanceFactory instanceFactory, boolean copyGeometries,
			Set<QName> skipChildren) {
		if (!(source instanceof Group)) {
			// source simple value
			if (targetDefinition.asProperty() != null) {
				// target can have value

				TypeDefinition propertyType = targetDefinition.asProperty().getPropertyType();
				if (copyGeometries || !isGeometry(source)) {

					if (propertyType.getChildren().isEmpty()) {
						// simple value
						return convertValue(source,
								targetDefinition.asProperty().getPropertyType());
					}
					else {
						// instance with value
						MutableInstance instance = instanceFactory.createInstance(propertyType);
						instance.setDataSet(DataSet.TRANSFORMED);
						instance.setValue(convertValue(source, propertyType));
						return instance;
					}
				}
				else {
					return Result.NO_MATCH;
				}
			}
		}

		// source is group or instance
		if (targetDefinition.asProperty() != null) {
			// target can have value

			TypeDefinition propertyType = targetDefinition.asProperty().getPropertyType();
			if (source instanceof Instance) {
				// source has value
				if (propertyType.getChildren().isEmpty()) {
					// simple value
					return convertValue(((Instance) source).getValue(),
							targetDefinition.asProperty().getPropertyType());
				}
				else {
					// instance with value
					MutableInstance instance = instanceFactory
							.createInstance(targetDefinition.asProperty().getPropertyType());
					instance.setDataSet(DataSet.TRANSFORMED);
					if (copyGeometries || !isGeometry(((Instance) source).getValue())) {
						instance.setValue(convertValue(((Instance) source).getValue(),
								targetDefinition.asProperty().getPropertyType()));
					}
					renameChildren((Group) source, instance, targetDefinition,
							allowIgnoreNamespaces, instanceFactory, copyGeometries, skipChildren);
					return instance;
				}
			}
			else {
				// source has no value
				if (targetDefinition.asProperty().getPropertyType().getChildren().isEmpty())
					return Result.NO_MATCH; // no match possible
				else {
					// instance with no value set
					MutableInstance instance = instanceFactory
							.createInstance(targetDefinition.asProperty().getPropertyType());
					instance.setDataSet(DataSet.TRANSFORMED);
					if (renameChildren((Group) source, instance, targetDefinition,
							allowIgnoreNamespaces, instanceFactory, copyGeometries, skipChildren))
						return instance;
					else
						return Result.NO_MATCH; // no child matched and no value
				}
			}

		}
		else if (targetDefinition.asGroup() != null) {
			// target can not have a value
			if (targetDefinition.asGroup().getDeclaredChildren().isEmpty())
				return Result.NO_MATCH; // target neither has a value nor
										// children?
			else {
				// group
				MutableGroup group = new DefaultGroup(targetDefinition.asGroup());
				if (renameChildren((Group) source, group, targetDefinition, allowIgnoreNamespaces,
						instanceFactory, copyGeometries, skipChildren))
					return group;
				else
					return Result.NO_MATCH; // no child matched and no value
			}
		}
		else {
			// neither asProperty nor asGroup -> illegal ChildDefinition
			throw new IllegalStateException("Illegal child type.");
		}
	}

	/**
	 * Determines if the given value is a geometry object.
	 * 
	 * @param value the value
	 * @return <code>true</code> if the value is a geometry object or
	 *         collection, false otherwise
	 */
	private static boolean isGeometry(Object value) {
		if (value instanceof GeometryProperty) {
			return true;
		}
		if (value instanceof Geometry) {
			return true;
		}

		if (value instanceof Collection<?>) {
			Collection<?> col = ((Collection<?>) value);
			if (!col.isEmpty()) {
				boolean other = false;
				for (Object element : col) {
					if (!(element instanceof GeometryProperty) && !(element instanceof Geometry)) {
						other = true;
						break;
					}
				}

				if (!other) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Tries to match any direct child of source group to the given
	 * targetDefinition. Matches are added to the given target group.
	 * 
	 * @param source the source group
	 * @param target the target group
	 * @param targetDefinition the target definition
	 * @param allowIgnoreNamespaces if for the structure comparison, namespaces
	 *            may be ignored
	 * @param instanceFactory the instance factory
	 * @param copyGeometries specifies if geometry objects should be copied
	 * @param skipChildren a set of direct children to skip or <code>null</code>
	 * @return true, if any property could be matched to the targetDefinition
	 */
	private static boolean renameChildren(Group source, MutableGroup target,
			ChildDefinition<?> targetDefinition, boolean allowIgnoreNamespaces,
			InstanceFactory instanceFactory, boolean copyGeometries, Set<QName> skipChildren) {
		boolean matchedChild = false;
		// walk over all source property names
		for (QName sourcePropertyName : source.getPropertyNames()) {
			if (skipChildren == null || !skipChildren.contains(sourcePropertyName)) {

				// find property name in target definition
				ChildDefinition<?> targetDefinitionChild = DefinitionUtil.getChild(targetDefinition,
						sourcePropertyName);

				if (targetDefinitionChild == null && allowIgnoreNamespaces) {
					// no corresponding target found
					// but we have the option to switch to another namespace
					targetDefinitionChild = DefinitionUtil.getChild(targetDefinition,
							sourcePropertyName, true);
				}

				if (targetDefinitionChild != null) {
					Object[] sourceProperties = source.getProperty(sourcePropertyName);
					// walk over all source property values
					for (Object sourceProperty : sourceProperties) {
						// try to match them
						Object result = structuralRename(sourceProperty, targetDefinitionChild,
								allowIgnoreNamespaces, instanceFactory, copyGeometries);
						if (result != Result.NO_MATCH) {
							// found match!
							target.addProperty(targetDefinitionChild.getName(), result);
							matchedChild = true;
						}
					}
				}
			}
		}
		return matchedChild;
	}

	/**
	 * Tries to convert the value to be compatible with targetType, returns the
	 * value itself if the conversion failed.
	 * 
	 * @param value the value to convert
	 * @param targetType the target type
	 * @return the converted value if successful, the original value otherwise
	 */
	private static Object convertValue(Object value, TypeDefinition targetType) {
		if (value == null)
			return null;

		Class<?> target = targetType.getConstraint(Binding.class).getBinding();

		if (target.isAssignableFrom(value.getClass()))
			return value;

		if (Collection.class.isAssignableFrom(target) && target.isAssignableFrom(List.class)) {
			// collection / list
			ElementType elementType = targetType.getConstraint(ElementType.class);
			try {
				return ConversionUtil.getAsList(value, elementType.getBinding(), true);
			} catch (ConversionException ce) {
				return value;
			}
		}

		try {
			return ConversionUtil.getAs(value, target);
		} catch (ConversionException ce) {
			return value;
		}
	}
}
