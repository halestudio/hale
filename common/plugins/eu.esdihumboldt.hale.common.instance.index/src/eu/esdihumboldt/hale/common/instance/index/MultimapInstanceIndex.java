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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;

/**
 * Simple implementation of an instance index based on {@link Multimap}s
 * 
 * @author Florian Esser
 */
public class MultimapInstanceIndex
		implements InstanceIndex<IndexedPropertyValue, PropertyEntityDefinitionMapping> {

	private final Multimap<IndexedPropertyValue, ResolvableInstanceReference> propertiesIndex = HashMultimap
			.create();
	private final Multimap<ResolvableInstanceReference, IndexedPropertyValue> instanceIndex = HashMultimap
			.create();

	private final List<PropertyEntityDefinitionMapping> mappings = new ArrayList<>();

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#clear()
	 */
	@Override
	public void clear() {
		propertiesIndex.clear();
		instanceIndex.clear();
		mappings.clear();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#addMapping(eu.esdihumboldt.hale.common.instance.index.IndexMapping)
	 */
	@Override
	public void addMapping(PropertyEntityDefinitionMapping mapping) {
		if (!mappings.stream().anyMatch(m -> m.getDefinition().getDefinition().getName()
				.equals(mapping.getDefinition().getDefinition().getName()))) {
			mappings.add(mapping);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#removeMapping(eu.esdihumboldt.hale.common.instance.index.IndexMapping)
	 */
	@Override
	public void removeMapping(PropertyEntityDefinitionMapping mapping) {
		QName propertyName = mapping.getDefinition().getDefinition().getName();
		propertiesIndex.asMap().entrySet()
				.removeIf(e -> e.getKey().getProperty().equals(propertyName));
		instanceIndex.values().removeIf(v -> v.getProperty().equals(propertyName));
		mappings.remove(mapping);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.Index#add(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void add(ResolvableInstanceReference reference, Instance instance) {
		mappings.forEach(m -> {
			IndexedPropertyValue propValue = m.map(instance);

			propertiesIndex.put(propValue, reference);
			instanceIndex.put(reference, propValue);
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
	public Collection<IndexedPropertyValue> getValues(ResolvableInstanceReference reference) {
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
	public Collection<Collection<ResolvableInstanceReference>> groupBy(List<QName> keyProperties) {
		Map<IndexedPropertyValue, Collection<ResolvableInstanceReference>> filteredProperties = propertiesIndex
				.asMap().entrySet().stream()
				.filter(entry -> keyProperties.contains(entry.getKey().getProperty()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

		return filteredProperties.values();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#find(java.lang.Object)
	 */
	@Override
	public Collection<ResolvableInstanceReference> find(IndexedPropertyValue value) {
		return propertiesIndex.get(value);
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
