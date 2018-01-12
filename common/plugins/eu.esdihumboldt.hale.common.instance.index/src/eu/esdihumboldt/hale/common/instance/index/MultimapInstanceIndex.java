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

import static eu.esdihumboldt.hale.common.instance.index.InstanceIndexUtil.collectionEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
public class MultimapInstanceIndex implements HaleInstanceIndex {

	private final Multimap<List<IndexedPropertyValue>, ResolvableInstanceReference> valueIndex = HashMultimap
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
		valueIndex.clear();
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

	/**
	 * @return an unmodifiable list of mappings
	 */
	@Override
	public Collection<PropertyEntityDefinitionMapping> getMappings() {
		return Collections.unmodifiableList(mappings);
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
		valueIndex.asMap().entrySet().removeIf(e -> collectionEquals(
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

			valueIndex.put(propValues, reference);
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
		throw new UnsupportedOperationException();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#getInstancePropertyValues(eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference)
	 */
	@Override
	public Collection<List<IndexedPropertyValue>> getInstancePropertyValues(
			ResolvableInstanceReference reference) {
		return Collections.unmodifiableCollection(instanceIndex.get(reference));
	}

	@Override
	public Collection<List<IndexedPropertyValue>> getInstancePropertyValuesById(Object instanceId) {
		Optional<ResolvableInstanceReference> optionalRef = instanceIndex.keySet().stream()
				.filter(ref -> ref.getId().equals(instanceId)).findFirst();

		if (optionalRef.isPresent()) {
			return instanceIndex.get(optionalRef.get());
		}
		else {
			return Collections.emptyList();
		}
	}

	@Override
	public Collection<ResolvableInstanceReference> getInstancesByValue(
			List<IndexedPropertyValue> values) {
		return Collections.unmodifiableCollection(valueIndex.get(values));
	}

	@Override
	public Collection<ResolvableInstanceReference> getInstancesByValue(List<QName> propertyPath,
			List<?> values) {

		// Find all IndexedPropertyValues in the key set of valueIndex where any
		// of the indexed value matches any of the provided values in the given
		// property
		List<List<IndexedPropertyValue>> matchingKeys = valueIndex.keySet().stream()
				.filter(ipvs -> ipvs.stream()
						.anyMatch(ipv -> ipv.getValues().stream()
								.anyMatch(v -> propertyPath.equals(ipv.getPropertyPath())
										&& ipv.getValues().contains(v))))
				.collect(Collectors.toList());

		Collection<ResolvableInstanceReference> result = new HashSet<>();
		matchingKeys.forEach(k -> result.addAll(valueIndex.get(k)));

		Iterator<ResolvableInstanceReference> it = result.iterator();
		while (it.hasNext()) {
			ResolvableInstanceReference ref = it.next();
			Collection<List<IndexedPropertyValue>> instValue = instanceIndex.get(ref);
			boolean remove = false;
			for (List<IndexedPropertyValue> ipvs : instValue) {
				for (IndexedPropertyValue ipv : ipvs) {
					if (ipv.getPropertyPath().equals(propertyPath)) {
						// Allow targets with any of the property values.
						if (!ipv.getValues().stream().anyMatch(v -> values.contains(v))) {
							remove = true;
						}
					}
				}
			}
			if (remove) {
				it.remove();
			}
		}

		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#getReferences()
	 */
	@Override
	public Collection<ResolvableInstanceReference> getReferences() {
		return Collections.unmodifiableSet(instanceIndex.keySet());
	}

	@Override
	public Collection<Collection<ResolvableInstanceReference>> groupBy(
			List<List<QName>> keyProperties) {

		List<Collection<ResolvableInstanceReference>> result = new ArrayList<>();
		for (List<IndexedPropertyValue> keyValues : valueIndex.keySet()) {
			List<List<QName>> kvProperties = keyValues.stream().map(ipv -> ipv.getPropertyPath())
					.collect(Collectors.toList());
			List<String> flatKvProperties = kvProperties.stream().map(
					e -> e.stream().map(qn -> qn.getLocalPart()).collect(Collectors.joining(".")))
					.collect(Collectors.toList());
			List<String> flatKeyProperties = keyProperties.stream().map(
					e -> e.stream().map(qn -> qn.getLocalPart()).collect(Collectors.joining(".")))
					.collect(Collectors.toList());
			if (collectionEquals(flatKvProperties, flatKeyProperties)) {
				result.add(valueIndex.get(keyValues));
			}
		}

		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndex#find(java.lang.Object)
	 */
	@Override
	public Collection<ResolvableInstanceReference> find(List<IndexedPropertyValue> value) {
		return valueIndex.get(value);
	}

}
