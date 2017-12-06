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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.IdentifiableInstance;
import eu.esdihumboldt.hale.common.instance.model.IdentifiableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;

/**
 * Maintains instance indexes
 * 
 * @author Florian Esser
 */
public class InstanceIndexServiceImpl implements InstanceIndexService {

	private final Map<QName, MultimapInstanceIndex> indexes = new HashMap<>();

	/**
	 * Create the index
	 */
	public InstanceIndexServiceImpl() {
	}

	@Override
	public void clearAll() {
		indexes.values().stream().forEach(i -> i.clearAll());
		indexes.clear();
	}

	@Override
	public void clearIndexedValues() {
		indexes.values().stream().forEach(i -> i.clearIndexes());
	}

	@Override
	public void add(Instance instance, ResolvableInstanceReference reference) {
		getIndex(instance.getDefinition().getName()).add(reference, instance);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexService#add(eu.esdihumboldt.hale.common.instance.model.Instance,
	 *      eu.esdihumboldt.hale.common.instance.model.InstanceCollection)
	 */
	@Override
	public void add(Instance instance, InstanceCollection instances) {
		InstanceReference ref;
		if (instance instanceof IdentifiableInstance) {
			Object id = ((IdentifiableInstance) instance).getId();
			ref = new IdentifiableInstanceReference(instances.getReference(instance), id);
		}
		else {
			ref = instances.getReference(instance);
		}

		ResolvableInstanceReference rir = new ResolvableInstanceReference(ref, instances);
		getIndex(instance.getDefinition().getName()).add(rir, instance);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexService#add(eu.esdihumboldt.hale.common.instance.model.InstanceReference,
	 *      eu.esdihumboldt.hale.common.instance.model.InstanceCollection)
	 */
	@Override
	public void add(InstanceReference reference, InstanceCollection instances) {
		Instance instance = instances.getInstance(reference);
		getIndex(instance.getDefinition().getName())
				.add(new ResolvableInstanceReference(reference, instances), instance);
	}

	@Override
	public void addPropertyMapping(List<PropertyEntityDefinition> propertyGroup) {
		if (propertyGroup.isEmpty()) {
			return;
		}

		QName type = propertyGroup.iterator().next().getType().getName();
		if (!propertyGroup.stream().allMatch(p -> p.getType().getName().equals(type))) {
			throw new IllegalArgumentException(
					"All properties in a group must be properties of the same type");
		}

		Set<PropertyEntityDefinition> keys = new HashSet<>();
		propertyGroup.forEach(
				p -> keys.add((PropertyEntityDefinition) AlignmentUtil.getAllDefaultEntity(p)));

		getIndex(type).addMapping(new PropertyEntityDefinitionMapping(keys));
	}

	@Override
	public void removePropertyMapping(List<PropertyEntityDefinition> properties) {
		if (properties.isEmpty()) {
			return;
		}

		QName type = properties.iterator().next().getType().getName();
		if (!properties.stream().allMatch(p -> p.getType().getName().equals(type))) {
			throw new IllegalArgumentException(
					"All properties in a group must be properties of the same type");
		}

		Set<PropertyEntityDefinition> keys = new HashSet<>();
		properties.forEach(
				p -> keys.add((PropertyEntityDefinition) AlignmentUtil.getAllDefaultEntity(p)));

		getIndex(type).removeMapping(new PropertyEntityDefinitionMapping(keys));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexService#find(javax.xml.namespace.QName,
	 *      java.util.Collection)
	 */
	@Override
	public InstanceCollection find(QName typeName, Collection<List<IndexedPropertyValue>> query) {
		MultimapInstanceIndex index = indexes.get(typeName);

		Collection<ResolvableInstanceReference> matchingRefs = new ArrayList<>();
		for (List<IndexedPropertyValue> valueGroup : query) {
			matchingRefs.addAll(index.find(valueGroup));
		}

		return new DefaultInstanceCollection(
				matchingRefs.stream().map(ref -> ref.resolve()).collect(Collectors.toList()));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexService#groupBy(javax.xml.namespace.QName,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Collection<ResolvableInstanceReference>> groupBy(QName typeName,
			List<List<QName>> properties) {
		MultimapInstanceIndex index = indexes.get(typeName);
		if (index == null) {
			return Collections.EMPTY_LIST;
		}

		return index.groupBy(properties);
	}

	private MultimapInstanceIndex getIndex(QName typeName) {
		if (!indexes.containsKey(typeName)) {
			indexes.put(typeName, new MultimapInstanceIndex());
		}

		return indexes.get(typeName);
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		clearAll();
		super.finalize();
	}

}
