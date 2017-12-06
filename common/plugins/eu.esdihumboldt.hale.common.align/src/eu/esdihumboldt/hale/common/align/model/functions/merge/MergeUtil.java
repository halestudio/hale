/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.align.model.functions.merge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Common utilities for getting and setting merge function parameters.
 * 
 * This is meant to be a single place where functionality is defined that is
 * used for the transformation (PropertiesMergeHandler), configuration in the UI
 * (MergeParameterPage) and cell migration.
 * 
 * @author Simon Templer
 */
public class MergeUtil {

	/**
	 * Get the properties defined in a Merge function property parameter.
	 * 
	 * @param parameters the parameters multimap
	 * @param parameterName the parameter name
	 * @return the list of properties identified by their property paths
	 */
	public static List<List<QName>> getProperties(ListMultimap<String, ParameterValue> parameters,
			String parameterName) {
		List<List<QName>> result = new ArrayList<List<QName>>();
		if (parameters.containsKey(parameterName)) {
			for (ParameterValue property : parameters.get(parameterName)) {
				result.add(getPropertyPath(property));
			}
		}
		return result;
	}

	/**
	 * Get the property path for a value representing a property configured as
	 * Merge function parameter.
	 * 
	 * @param value the value representation of the parameter
	 * @return the property identified by its property path
	 */
	public static List<QName> getPropertyPath(Value value) {
		// XXX removed because it causes problems with dots in property names
//		return PropertyResolver.getQNamesFromPath(value.as(String.class));
		// FIXME quick fix that only works because only first level properties
		// are supported
		return Collections.singletonList(QName.valueOf(value.as(String.class)));
	}

	/**
	 * Get the property path for a value representing a property configured as
	 * Merge function parameter and resolve it to an entity definition.
	 * 
	 * @param value the value representation of the parameter
	 * @param parentType the parent type of the property (the type that is
	 *            merged)
	 * @return the entity definition resolved from the property path
	 * @throws IllegalStateException if the property path cannot be resolved
	 */
	public static EntityDefinition resolvePropertyPath(Value value, TypeDefinition parentType) {
		List<QName> propertyPath = MergeUtil.getPropertyPath(value);

		List<ChildDefinition<?>> path = new ArrayList<>();
		Definition<?> parent = parentType;
		for (QName element : propertyPath) {
			ChildDefinition<?> child = DefinitionUtil.getChild(parent, element);
			if (child != null) {
				path.add(child);
				parent = child;
			}
			else {
				throw new IllegalStateException(
						"Could not resolve child " + element + " in parent " + parent);
			}
		}

		if (path.isEmpty()) {
			throw new IllegalStateException("No elements in property path");
		}

		List<ChildContext> contexts = path.stream().map(ChildContext::new)
				.collect(Collectors.toList());

		return AlignmentUtil.createEntity(parentType, contexts, SchemaSpaceID.SOURCE, null);
	}

	/**
	 * Convert a property path to a value to be used for a parameter.
	 * 
	 * @param path the property path
	 * @return the representation a parameter value
	 */
	public static ParameterValue toPropertyParameter(List<QName> path) {
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("Property path may not be empty");
		}

		// FIXME only works for 1-level properties
		if (path.size() > 1) {
			throw new IllegalStateException("Only properties w/ one level supported currently");
		}

		// TODO instead representation via ValueList and QName?

		QName property = path.get(0);
		return new ParameterValue(property.toString());
	}

	/**
	 * Get the {@link PropertyEntityDefinition} paths for all key properties of
	 * a merge.<br>
	 * <br>
	 * <b>Subproperties are not yet supported to be part of a merge key.
	 * Therefore, the inner lists will contain only a single property for the
	 * time being.</b>
	 * 
	 * @param cell Mapping cell of the merge
	 * @return <code>PropertyEntityDefinition</code> paths for all key
	 *         properties of the merge
	 */
	public static List<PropertyEntityDefinition> getKeyPropertyDefinitions(Cell cell) {
		if (!cell.getTransformationIdentifier().equals(MergeFunction.ID)) {
			throw new IllegalArgumentException("This method applies only to Merge transformations");
		}

		List<PropertyEntityDefinition> result = new ArrayList<>();

		List<List<QName>> mergeProperties = MergeUtil.getProperties(
				cell.getTransformationParameters(), MergeFunction.PARAMETER_PROPERTY);
		for (Entity sourceEntity : cell.getSource().values()) {
			PropertyEntityDefinition keyProperty = null;
			for (List<QName> mergePropertyPath : mergeProperties) {
				// TODO Only root property is considered for now
				// If the propertyPath can consist of more than one element,
				// make sure to construct the PropertyEntityDefinition
				// accordingly
				QName root = mergePropertyPath.get(0);
				for (PropertyEntityDefinition property : AlignmentUtil
						.getChildrenWithoutContexts(sourceEntity)) {
					if (property.getDefinition().getName().equals(root)) {
						keyProperty = property;
						break;
					}
				}
				if (keyProperty != null) {
					result.add(keyProperty);
				}
			}
		}

		return result;
	}

}
