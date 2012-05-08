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

package eu.esdihumboldt.cst.functions.core.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.FamilyInstance;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceFactory;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Abstract merge handler implementation based on a merge index of instance
 * references.
 * @param <T> the merge configuration type 
 * @param <K> the merge key type
 * @author Simon Templer
 */
public abstract class AbstractMergeHandler<T, K> implements InstanceHandler<TransformationEngine> {

	/**
	 * Resource iterator over the merged instances
	 */
	public class MergedIterator extends GenericResourceIteratorAdapter<K, FamilyInstance> {
		private final Multimap<K, InstanceReference> index;
		private final InstanceCollection originalInstances;
		private final T mergeConfig;

		/**
		 * Create a collection of merged instances.
		 * @param index the merge index
		 * @param instances the original instance collection
		 * @param mergeConfig the merge configuration
		 */
		public MergedIterator(Multimap<K, InstanceReference> index,
				InstanceCollection instances, T mergeConfig) {
			super(index.keySet().iterator());
			this.index = index;
			this.originalInstances = instances;
			this.mergeConfig = mergeConfig;
		}

		@Override
		protected FamilyInstance convert(K next) {
			// next is the merge key

			// get the instances to merge
			Collection<InstanceReference> references = index.get(next);
			//TODO get all instances in one call from instance collection? see InstanceResolver
			Collection<Instance> instances = new ArrayList<Instance>(references.size());
			TypeDefinition type = null;
			for (InstanceReference ref : references) {
				Instance instance = originalInstances.getInstance(ref);
				if (instance == null) {
					throw new IllegalStateException(
							"Instance reference could not be resolved");
				} else {
					instances.add(instance);
					if (type == null) {
						type = instance.getDefinition();
					}
				}
			}

			return new FamilyInstanceImpl(merge(instances, type, next, mergeConfig));
		}

		@Override
		public void remove() {
			// prohibit remove
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @see InstanceHandler#partitionInstances(InstanceCollection, String, TransformationEngine, ListMultimap, Map, TransformationLog)
	 */
	@Override
	public ResourceIterator<FamilyInstance> partitionInstances(InstanceCollection instances,
			String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, String> transformationParameters,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {
		T mergeConfig = createMergeConfiguration(transformationIdentifier, 
				transformationParameters, executionParameters, log);
		
		// create merge index over all instances (references)
		Multimap<K, InstanceReference> index = HashMultimap.create();
		
		ResourceIterator<Instance> it = instances.iterator();
		try {
			while (it.hasNext()) {
				Instance instance = it.next();
				K key = getMergeKey(instance, mergeConfig);
				index.put(key, instances.getReference(instance));
			}
		} finally {
			it.close();
		}
		
		return new MergedIterator(index, instances, mergeConfig);
	}
	
	/**
	 * Get the instance factory
	 * @return the instance factory
	 */
	protected InstanceFactory getInstanceFactory() {
		return OsgiUtils.getService(InstanceFactory.class);
	}

	/**
	 * Create the merge configuration from the transformation configuration.
	 * The merge configuration may be then used in {@link #getMergeKey(Instance, Object)}
	 * and {@link #merge(Collection, TypeDefinition, Object, Object)}
	 * @param transformationIdentifier the transformation identifier
	 * @param transformationParameters the transformation parameters
	 * @param executionParameters the execution parameters
	 * @param log the transformation log
	 * @return the merge configuration
	 * @throws TransformationException if the merge configuration cannot be created
	 */
	protected abstract T createMergeConfiguration(String transformationIdentifier,
			ListMultimap<String, String> transformationParameters,
			Map<String, String> executionParameters, TransformationLog log) throws TransformationException;

	/**
	 * Get the merge key for a given instance. Instances with an equal merge
	 * key will be merged.
	 * @param instance the instance
	 * @param mergeConfig the merge configuration
	 * @return the instance merge key
	 * @see #merge(Collection, TypeDefinition, Object, Object)
	 */
	protected abstract K getMergeKey(Instance instance,
			T mergeConfig);
	
	/**
	 * Merge multiple instance into one.
	 * @param instances the instances to merge
	 * @param type the type definition of the instances to merge
	 * @param mergeKey the merge key associated to the instances
	 * @param mergeConfig the merge configuration
	 * @return the merged instance
	 */
	protected abstract Instance merge(Collection<Instance> instances, 
			TypeDefinition type, K mergeKey, T mergeConfig);

}
