/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instance.index;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.google.common.collect.Multiset;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Mapping definition between {@link Instance} and {@link IndexedPropertyValue}
 * based on a {@link PropertyEntityDefinition}.
 * 
 * @author Florian Esser
 */
public class PropertyEntityDefinitionMapping
		implements IndexMapping<Instance, List<IndexedPropertyValue>> {

	private final Set<PropertyEntityDefinition> definitions = new HashSet<>();

	/**
	 * Create the mapping based on the given property
	 * 
	 * @param definitions Property entity definitions
	 */
	public PropertyEntityDefinitionMapping(Set<PropertyEntityDefinition> definitions) {
		this.definitions.addAll(definitions);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.IndexMapping#map(java.lang.Object)
	 */
	@Override
	public List<IndexedPropertyValue> map(Instance instance) {
		List<IndexedPropertyValue> result = new ArrayList<>();

		for (PropertyEntityDefinition definition : definitions) {
			List<QName> propertyPath = definition.getPropertyPath().stream()
					.map(cctx -> cctx.getChild().getName()).collect(Collectors.toList());
			Multiset<?> values = AlignmentUtil.getValues(instance, definition, false);
			result.add(new IndexedPropertyValue(propertyPath, values.elementSet().stream()
					.map(v -> processValue(v, definition)).collect(Collectors.toList())));
		}

		return result;
	}

	/**
	 * @return the property entity definition
	 */
	public Set<PropertyEntityDefinition> getDefinitions() {
		return definitions;
	}

	private static Object processValue(Object value, PropertyEntityDefinition property) {
		if (value instanceof Instance) {
			return new DeepIterableKey(value);
		}

		return InstanceIndexUtil.processValue(value, property);
	}
}
