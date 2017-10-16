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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
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
	public void clear() {
		indexes.values().stream().forEach(i -> i.clear());
		indexes.clear();
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
		instances.iterator().forEachRemaining(inst -> {
			ResolvableInstanceReference ref = new ResolvableInstanceReference(
					instances.getReference(inst), instances);
			getIndex(instance.getDefinition().getName()).add(ref, inst);
		});
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

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexService#addPropertyMapping(java.util.Collection)
	 */
	@Override
	public void addPropertyMappings(Collection<PropertyEntityDefinition> properties) {
		properties.forEach(
				p -> getIndex(p.getType().getName()).addMapping(new PropertyEntityDefinitionMapping(
						(PropertyEntityDefinition) AlignmentUtil.getAllDefaultEntity(p))));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexService#removePropertyMappings(java.util.Collection)
	 */
	@Override
	public void removePropertyMappings(Collection<PropertyEntityDefinition> properties) {
		properties.forEach(p -> getIndex(p.getType().getName())
				.removeMapping(new PropertyEntityDefinitionMapping(
						(PropertyEntityDefinition) AlignmentUtil.getAllDefaultEntity(p))));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexService#find(javax.xml.namespace.QName,
	 *      java.util.Collection)
	 */
	@Override
	public InstanceCollection find(QName typeName, Collection<IndexedPropertyValue> query) {
		MultimapInstanceIndex index = indexes.get(typeName);

		Collection<ResolvableInstanceReference> matchingRefs = new ArrayList<>();
		for (IndexedPropertyValue ipv : query) {
			matchingRefs.addAll(index.find(ipv));
		}

		return new DefaultInstanceCollection(
				matchingRefs.stream().map(ref -> ref.resolve()).collect(Collectors.toList()));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexService#groupBy(javax.xml.namespace.QName,
	 *      java.util.List)
	 */
	@Override
	public Collection<Collection<ResolvableInstanceReference>> groupBy(QName typeName,
			List<QName> properties) {
		MultimapInstanceIndex index = indexes.get(typeName);
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
		clear();
		super.finalize();
	}

}
