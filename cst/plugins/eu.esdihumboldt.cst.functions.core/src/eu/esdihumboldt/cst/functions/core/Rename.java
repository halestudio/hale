/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.functions.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import net.jcip.annotations.Immutable;

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
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.OGroup;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;

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

		String structuralRename = getOptionalParameter(PARAMETER_STRUCTURAL_RENAME, "false");
		boolean structuralRenameEnabled = Boolean.parseBoolean(structuralRename);

		String ignoreNamespaces = getOptionalParameter(PARAMETER_IGNORE_NAMESPACES, "false");
		boolean ignoreNamespacesEnabled = Boolean.parseBoolean(ignoreNamespaces);

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
					ignoreNamespacesEnabled);
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
	 * @return the transformed value (or group/instance) or NO_MATCH
	 */
	private Object structuralRename(Object source, ChildDefinition<?> targetDefinition,
			boolean allowIgnoreNamespaces) {
		if (!(source instanceof Group)) {
			// source simple value
			if (targetDefinition.asProperty() != null) {
				// target can have value
				if (targetDefinition.asProperty().getPropertyType().getChildren().isEmpty()) {
					// simple value
					return convertValue(source, targetDefinition.asProperty().getPropertyType());
				}
				else {
					// instance with value
					MutableInstance instance = new DefaultInstance(targetDefinition.asProperty()
							.getPropertyType(), DataSet.TRANSFORMED);
					instance.setValue(convertValue(source, targetDefinition.asProperty()
							.getPropertyType()));
					return instance;
				}
			}
		}

		// source is group or instance
		if (targetDefinition.asProperty() != null) {
			// target can have value
			if (source instanceof Instance) {
				// source has value
				if (targetDefinition.asProperty().getPropertyType().getChildren().isEmpty()) {
					// simple value
					return convertValue(((Instance) source).getValue(), targetDefinition
							.asProperty().getPropertyType());
				}
				else {
					// instance with value
					MutableInstance instance = new DefaultInstance(targetDefinition.asProperty()
							.getPropertyType(), DataSet.TRANSFORMED);
					instance.setValue(convertValue(((Instance) source).getValue(), targetDefinition
							.asProperty().getPropertyType()));
					renameChildren((Group) source, instance, targetDefinition,
							allowIgnoreNamespaces);
					return instance;
				}
			}
			else {
				// source has no value
				if (targetDefinition.asProperty().getPropertyType().getChildren().isEmpty())
					return Result.NO_MATCH; // no match possible
				else {
					// instance with no value set
					MutableInstance instance = new DefaultInstance(targetDefinition.asProperty()
							.getPropertyType(), DataSet.TRANSFORMED);
					if (renameChildren((Group) source, instance, targetDefinition,
							allowIgnoreNamespaces))
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
				MutableGroup group = new OGroup(targetDefinition.asGroup());
				if (renameChildren((Group) source, group, targetDefinition, allowIgnoreNamespaces))
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
	 * Tries to match any direct child of source group to the given
	 * targetDefinition. Matches are added to the given target group.
	 * 
	 * @param source the source group
	 * @param target the target group
	 * @param targetDefinition the target definition
	 * @param allowIgnoreNamespaces if for the structure comparison, namespaces
	 *            may be ignored
	 * @return true, if any property could be matched to the targetDefinition
	 */
	private boolean renameChildren(Group source, MutableGroup target,
			ChildDefinition<?> targetDefinition, boolean allowIgnoreNamespaces) {
		boolean matchedChild = false;
		// walk over all source property names
		for (QName sourcePropertyName : source.getPropertyNames()) {
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
							allowIgnoreNamespaces);
					if (result != Result.NO_MATCH) {
						// found match!
						target.addProperty(targetDefinitionChild.getName(), result);
						matchedChild = true;
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
	private Object convertValue(Object value, TypeDefinition targetType) {
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
