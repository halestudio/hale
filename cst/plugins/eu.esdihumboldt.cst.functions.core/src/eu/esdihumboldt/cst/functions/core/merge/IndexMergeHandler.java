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

package eu.esdihumboldt.cst.functions.core.merge;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.functions.merge.MergeUtil;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.core.service.ServiceProviderAware;
import eu.esdihumboldt.hale.common.instance.index.DeepIterableKey;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexService;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Identifiable;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceFactory;
import eu.esdihumboldt.hale.common.instance.model.InstanceMetadata;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceDecorator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Merge using an instance index
 * 
 * @author Florian Esser
 */
public class IndexMergeHandler
		implements MergeFunction, ServiceProviderAware, InstanceHandler<TransformationEngine> {

	private ServiceProvider serviceProvider;

	class IndexMergeConfig {

		protected final List<List<QName>> keyProperties;
		protected final List<List<QName>> additionalProperties;
		protected final boolean autoDetect;

		protected IndexMergeConfig(List<List<QName>> keyProperties,
				List<List<QName>> additionalProperties, boolean autoDetect) {
			super();
			this.keyProperties = keyProperties;
			this.additionalProperties = additionalProperties;
			this.autoDetect = autoDetect;
		}
	}

	/**
	 * @see eu.esdihumboldt.cst.functions.core.merge.AbstractMergeHandler#partitionInstances(eu.esdihumboldt.hale.common.instance.model.InstanceCollection,
	 *      java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine,
	 *      com.google.common.collect.ListMultimap, java.util.Map,
	 *      eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	public ResourceIterator<FamilyInstance> partitionInstances(InstanceCollection instances,
			String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, ParameterValue> transformationParameters,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {

		PropertiesMergeHandler fallbackHandler = new PropertiesMergeHandler();

		InstanceIndexService indexService = serviceProvider.getService(InstanceIndexService.class);
		if (indexService == null) {
			log.warn(MessageFormat.format(
					"Index service not available, falling back to merge handler {0}",
					fallbackHandler.getClass().getCanonicalName()));
			return fallbackHandler.partitionInstances(instances, transformationIdentifier, engine,
					transformationParameters, executionParameters, log);
		}

		final IndexMergeConfig mergeConfig = createMergeConfiguration(transformationParameters);
		QName typeName;
		try (ResourceIterator<Instance> it = instances.iterator()) {
			if (it.hasNext()) {
				typeName = it.next().getDefinition().getName();
			}
			else {
				// Nothing to partition
				return new ResourceIterator<FamilyInstance>() {

					@Override
					public boolean hasNext() {
						return false;
					}

					@Override
					public FamilyInstance next() {
						return null;
					}

					@Override
					public void close() {
						// Do nothing
					}

				};
			}
		}

		// Querying the index will yield a result over all instances. We must,
		// however, be able to operate only on the given input instances instead
		// of all instances.
		// We must, therefore, be able to uniquely identify every instance in
		// the index, so that we can retain from the index query only the
		// relevant instances.
		List<Object> inputInstanceIds = new ArrayList<>();
		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext()) {
				Instance i = InstanceDecorator.getRoot(it.next());
				if (!Identifiable.is(i)) {
					log.warn(MessageFormat.format(
							"At least one instance does not have an ID, falling back to merge handler {0}",
							fallbackHandler.getClass().getCanonicalName()));
					return fallbackHandler.partitionInstances(instances, transformationIdentifier,
							engine, transformationParameters, executionParameters, log);
				}
				inputInstanceIds.add(Identifiable.getId(i));
			}
		}

		Collection<Collection<ResolvableInstanceReference>> partitionedIndex = indexService
				.groupBy(typeName, mergeConfig.keyProperties);

		// Remove instance groups from the partitioned index where none of the
		// instances in the group are in the processed instances.
		partitionedIndex.removeIf(part -> !part.stream().map(ref -> ref.getId())
				.anyMatch(id -> inputInstanceIds.contains(id)));

		Iterator<Collection<ResolvableInstanceReference>> it = partitionedIndex.iterator();
		return new ResourceIterator<FamilyInstance>() {

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public FamilyInstance next() {
				Collection<ResolvableInstanceReference> instanceRefs = it.next();
				InstanceCollection instancesToBeMerged = new DefaultInstanceCollection(instanceRefs
						.stream().map(ref -> ref.resolve()).collect(Collectors.toList()));

				return new FamilyInstanceImpl(merge(instancesToBeMerged, mergeConfig));
			}

			@Override
			public void close() {
				// TODO Auto-generated method stub

			}

		};

	}

	private Instance merge(InstanceCollection instances, IndexMergeConfig mergeConfig) {
		TypeDefinition type;

		try (ResourceIterator<Instance> it = instances.iterator()) {
			if (instances.hasSize() && instances.size() == 1) {
				// early exit if only one instance to merge
				return it.next();
			}
			else {
				type = it.next().getDefinition();
			}
		}

		MutableInstance result = getInstanceFactory().createInstance(type);

		/*
		 * FIXME This a first VERY basic implementation, where only the first
		 * item in each property path is regarded, and that whole tree is added
		 * only once (from the first instance). XXX This especially will be a
		 * problem, if a path contains a choice. XXX For more advanced stuff we
		 * need more advanced test cases.
		 */
		Set<QName> rootNames = new HashSet<QName>();
		Set<QName> nonKeyRootNames = new HashSet<QName>();
		// collect path roots
		for (List<QName> path : mergeConfig.keyProperties) {
			rootNames.add(path.get(0));
		}
		for (List<QName> path : mergeConfig.additionalProperties) {
			nonKeyRootNames.add(path.get(0));
		}

		// XXX what about metadata?!
		// XXX for now only retain IDs
		Set<Object> ids = new HashSet<Object>();

		try (ResourceIterator<Instance> it = instances.iterator()) {
			while (it.hasNext()) {
				Instance instance = it.next();

				for (QName name : instance.getPropertyNames()) {
					if (rootNames.contains(name)) {
						/*
						 * Property is merge key -> only use first occurrence
						 * (as all entries need to be the same)
						 * 
						 * TODO adapt if multiple keys are possible per instance
						 */
						addFirstOccurrence(result, instance, name);
					}
					else if (nonKeyRootNames.contains(name)) {
						/*
						 * Property is additional merge property.
						 * 
						 * Traditional behavior: Only keep unique values.
						 * 
						 * XXX should this be configurable?
						 */
						addUnique(result, instance, name);
					}
					else if (mergeConfig.autoDetect) {
						/*
						 * Auto-detection is enabled.
						 * 
						 * Only keep unique values.
						 * 
						 * XXX This differs from the traditional behavior in
						 * that there only the first value would be used, but
						 * only if all values were equal. That cannot be easily
						 * checked in an iterative approach.
						 */
						addUnique(result, instance, name);
					}
					else {
						/*
						 * Property is not to be merged.
						 * 
						 * XXX but we could do some kind of aggregation
						 * 
						 * XXX for now just add all values
						 */
						addValues(result, instance, name);
					}
				}

				List<Object> instanceIDs = instance.getMetaData(InstanceMetadata.METADATA_ID);
				for (Object id : instanceIDs) {
					ids.add(id);
				}
			}
		}

		// store metadata IDs
		result.setMetaData(InstanceMetadata.METADATA_ID, ids.toArray());

		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.service.ServiceProviderAware#setServiceProvider(eu.esdihumboldt.hale.common.core.service.ServiceProvider)
	 */
	@Override
	public void setServiceProvider(ServiceProvider services) {
		this.serviceProvider = services;
	}

	/**
	 * Apply instance property values to the merged result instance. Use the
	 * "first occurrence" strategy that only keeps the values from the first
	 * instance.
	 * 
	 * @param result the result instance
	 * @param instance the instance to merge with the result
	 * @param property the name of the property that should be handled
	 */
	private void addFirstOccurrence(MutableInstance result, Instance instance, QName property) {
		Object[] existingValues = result.getProperty(property);

		if (existingValues == null || existingValues.length <= 0) {
			// no values yet -> add values
			addValues(result, instance, property);
		}
	}

	/**
	 * Apply instance property values to the merged result instance. Use the
	 * "unique" strategy that only keeps unique values.
	 * 
	 * @param result the result instance
	 * @param instance the instance to merge with the result
	 * @param property the name of the property that should be handled
	 */
	private void addUnique(MutableInstance result, Instance instance, QName property) {
		Object[] values = instance.getProperty(property);
		if (values == null || values.length <= 0) {
			return;
		}

		// collect unique values
		Object[] existingValues = result.getProperty(property);
		Set<DeepIterableKey> uniqueValues = new HashSet<>();
		if (existingValues != null) {
			for (Object value : existingValues) {
				uniqueValues.add(new DeepIterableKey(value));
			}
		}

		// add values not contained yet
		for (Object value : values) {
			DeepIterableKey key = new DeepIterableKey(value);
			if (uniqueValues.add(key)) {
				result.addProperty(property, value);
			}
		}
	}

	/**
	 * Apply instance property values to the merged result instance. Use the
	 * "add values" strategy that keeps all values.
	 * 
	 * @param result the result instance
	 * @param instance the instance to merge with the result
	 * @param property the name of the property that should be handled
	 */
	private void addValues(MutableInstance result, Instance instance, QName property) {
		// add all values
		Object[] values = instance.getProperty(property);
		if (values != null) {
			for (Object value : values) {
				result.addProperty(property, value);
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean allEqual(List<Object[]> list) {
		Iterator<Object[]> iter = list.iterator();
		// get first element
		DeepIterableKey first = new DeepIterableKey(iter.next());
		// compare rest to first
		while (iter.hasNext())
			if (!first.equals(new DeepIterableKey(iter.next())))
				return false;
		return true;
	}

	/**
	 * Get the instance factory
	 * 
	 * @return the instance factory
	 */
	protected InstanceFactory getInstanceFactory() {
		return HalePlatform.getService(InstanceFactory.class);
	}

	private IndexMergeConfig createMergeConfiguration(
			ListMultimap<String, ParameterValue> transformationParameters)
			throws TransformationException {
		if (transformationParameters == null) {
			throw new TransformationException("Transformation parameters invalid");
		}

		List<List<QName>> properties = MergeUtil.getProperties(transformationParameters,
				PARAMETER_PROPERTY);

		List<List<QName>> additionalProperties = MergeUtil.getProperties(transformationParameters,
				PARAMETER_ADDITIONAL_PROPERTY);

		boolean autoDetect;
		if (transformationParameters.get(PARAMETER_AUTO_DETECT).isEmpty()) {
			// default to false (original behavior)
			autoDetect = false;
		}
		else {
			autoDetect = Boolean.parseBoolean(
					transformationParameters.get(PARAMETER_AUTO_DETECT).get(0).as(String.class));
		}

		return new IndexMergeConfig(properties, additionalProperties, autoDetect);
	}

}
