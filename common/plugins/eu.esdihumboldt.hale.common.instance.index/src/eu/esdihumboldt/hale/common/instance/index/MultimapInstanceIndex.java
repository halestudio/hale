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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;

/**
 * Simple implementation of an instance index based on {@link Multimap}s
 * 
 * @author Florian Esser
 */
public class MultimapInstanceIndex
		implements InstanceIndex<List<IndexedPropertyValue>, PropertyEntityDefinitionMapping> {

	private final Multimap<List<IndexedPropertyValue>, ResolvableInstanceReference> propertiesIndex = HashMultimap
			.create();
	private final Multimap<ResolvableInstanceReference, List<IndexedPropertyValue>> instanceIndex = HashMultimap
			.create();

	private final List<PropertyEntityDefinitionMapping> mappings = new ArrayList<>();

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#clearAll()
	 */
	@Override
	public void clearAll() {
		clearIndexes();
		mappings.clear();
	}

	@Override
	public void clearIndexes() {
		propertiesIndex.clear();
		instanceIndex.clear();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#addMapping(eu.esdihumboldt.hale.common.instance.index.IndexMapping)
	 */
	@Override
	public void addMapping(PropertyEntityDefinitionMapping mapping) {
		if (!mappings.stream()
				.anyMatch(m -> collectionEquals(m.getDefinitions(), mapping.getDefinitions()))) {
			mappings.add(mapping);
		}
	}

	private static boolean collectionEquals(Collection<?> c1, Collection<?> c2) {
		return c1.containsAll(c2) && c2.containsAll(c1);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#removeMapping(eu.esdihumboldt.hale.common.instance.index.IndexMapping)
	 */
	@Override
	public void removeMapping(PropertyEntityDefinitionMapping mapping) {
		// extract property names from mapping
		List<QName> propertyNames = mapping.getDefinitions().stream()
				.map(m -> m.getDefinition().getName()).collect(Collectors.toList());

		// Remove from properties index if key properties are equal to
		// properties in mapping
		propertiesIndex.asMap().entrySet().removeIf(e -> collectionEquals(
				e.getKey().stream().map(ipv -> ipv.getPropertyPath()).collect(Collectors.toList()),
				propertyNames));

		// Remove mapped instance values
		instanceIndex.asMap().values()
				.forEach(
						instValues -> instValues
								.removeIf(
										instValue -> collectionEquals(
												instValue.stream().map(iv -> iv.getPropertyPath())
														.collect(Collectors.toList()),
												propertyNames)));

		instanceIndex.asMap().entrySet().removeIf(e -> e.getValue().isEmpty());

		mappings.removeIf(m -> collectionEquals(m.getDefinitions(), mapping.getDefinitions()));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.Index#add(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void add(ResolvableInstanceReference reference, Instance instance) {
		mappings.forEach(m -> {
			List<IndexedPropertyValue> propValues = m.map(instance);

			propertiesIndex.put(propValues, reference);
			instanceIndex.put(reference, propValues);
		});
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.Index#get(java.lang.Object)
	 */
	@Override
	public Instance get(ResolvableInstanceReference ref) {
		return ResolvableInstanceReference.tryResolve(ref);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.Index#search(java.lang.Object)
	 */
	@Override
	public Collection<ResolvableInstanceReference> search(InstanceIndexQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#getValues(eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference)
	 */
	@Override
	public Collection<List<IndexedPropertyValue>> getValues(ResolvableInstanceReference reference) {
		return Collections.unmodifiableCollection(instanceIndex.get(reference));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#getReferences()
	 */
	@Override
	public Collection<ResolvableInstanceReference> getReferences() {
		return Collections.unmodifiableSet(instanceIndex.keySet());
	}

	/**
	 * Retrieve instance references from the index grouped by the given
	 * properties.
	 * 
	 * @param keyProperties Properties to group by
	 * @return Grouped instance references
	 */
	public Collection<Collection<ResolvableInstanceReference>> groupBy(
			List<List<QName>> keyProperties) {

		List<Collection<ResolvableInstanceReference>> result = new ArrayList<>();
		for (List<IndexedPropertyValue> keyValues : propertiesIndex.keySet()) {
			List<List<QName>> kvProperties = keyValues.stream().map(ipv -> ipv.getPropertyPath())
					.collect(Collectors.toList());
			List<String> flatKvProperties = kvProperties.stream().map(
					e -> e.stream().map(qn -> qn.getLocalPart()).collect(Collectors.joining(".")))
					.collect(Collectors.toList());
			List<String> flatKeyProperties = keyProperties.stream().map(
					e -> e.stream().map(qn -> qn.getLocalPart()).collect(Collectors.joining(".")))
					.collect(Collectors.toList());
			if (collectionEquals(flatKvProperties, flatKeyProperties)) {
				result.add(propertiesIndex.get(keyValues));
			}
		}

		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#find(java.lang.Object)
	 */
	@Override
	public Collection<ResolvableInstanceReference> find(List<IndexedPropertyValue> value) {
		return propertiesIndex.get(value);
	}

	/**
	 * @return an unmodifiable list of mappings
	 */
	public List<PropertyEntityDefinitionMapping> getMappings() {
		return Collections.unmodifiableList(mappings);
	}

//	public List<List<IndexedPropertyValue>> findUniqueValueCombos(List<QName> keyProperties) {
//		List<ResolvableInstanceReference> candidates = new ArrayList<>();
//
//		// Find candidate instances that have all key properties
//		candidates.addAll(instanceIndex.asMap().entrySet().stream().filter(instEntry -> {
//			return keyProperties.stream().allMatch(keyProperty -> instEntry.getValue().stream()
//					.anyMatch(ipv -> keyProperty.equals(ipv.getProperty())));
//		}).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())).keySet());
//
//		List<List<IndexedPropertyValue>> result = new ArrayList<>();
//		for (QName keyProperty : keyProperties) {
//			if (result.isEmpty()) {
//				Set<IndexedPropertyValue> uniqueValues = candidates.stream().filter(cand -> {
//					instanceIndex.get(cand);
//				}).collect(Collectors.toSet());
//				result.addAll(uniqueValues.stream().map(val -> {
//					List<IndexedPropertyValue> list = new ArrayList<>();
//					list.add(val);
//				}));
//
//			}
//
//		}
//	}

}
