/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.core.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceMetadata;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Merge based on equal properties.
 * 
 * @author Simon Templer
 */
public class PropertiesMergeHandler extends
		AbstractMergeHandler<PropertiesMergeHandler.PropertiesMergeConfig, DeepIterableKey>
		implements MergeFunction {

	class PropertiesMergeConfig {

		private final List<List<QName>> keyProperties;
		private final List<List<QName>> additionalProperties;
		private final boolean autoDetect;

		private PropertiesMergeConfig(List<List<QName>> keyProperties,
				List<List<QName>> additionalProperties, boolean autoDetect) {
			super();
			this.keyProperties = keyProperties;
			this.additionalProperties = additionalProperties;
			this.autoDetect = autoDetect;
		}
	}

	@Override
	protected PropertiesMergeConfig createMergeConfiguration(String transformationIdentifier,
			ListMultimap<String, ParameterValue> transformationParameters,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {
		if (transformationParameters == null) {
			throw new TransformationException("Transformation parameters invalid");
		}

		List<List<QName>> properties = new ArrayList<List<QName>>();
		if (transformationParameters.containsKey(PARAMETER_PROPERTY)
				&& !transformationParameters.get(PARAMETER_PROPERTY).isEmpty()) {
			for (ParameterValue property : transformationParameters.get(PARAMETER_PROPERTY)) {
				properties.add(PropertyResolver.getQNamesFromPath(property.as(String.class)));
			}
		}

		List<List<QName>> additionalProperties = new ArrayList<List<QName>>();
		if (transformationParameters.containsKey(PARAMETER_ADDITIONAL_PROPERTY)) {
			for (ParameterValue property : transformationParameters
					.get(PARAMETER_ADDITIONAL_PROPERTY)) {
				additionalProperties.add(PropertyResolver.getQNamesFromPath(property
						.as(String.class)));
			}
		}

		boolean autoDetect;
		if (transformationParameters.get(PARAMETER_AUTO_DETECT).isEmpty()) {
			// default to false (original behavior)
			autoDetect = false;
		}
		else {
			autoDetect = Boolean.parseBoolean(transformationParameters.get(PARAMETER_AUTO_DETECT)
					.get(0).as(String.class));
		}

		return new PropertiesMergeConfig(properties, additionalProperties, autoDetect);
	}

	@Override
	protected DeepIterableKey getMergeKey(Instance instance, PropertiesMergeConfig mergeConfig) {
		if (mergeConfig.keyProperties.isEmpty()) {
			// merge all instances
			return new DeepIterableKey(Long.valueOf(1)); // XXX Hack Any value.
		}

		List<Object> valueList = new ArrayList<Object>(mergeConfig.keyProperties.size());

		for (List<QName> propertyPath : mergeConfig.keyProperties) {
			String query = Joiner.on('.').join(
					Collections2.transform(propertyPath, new Function<QName, String>() {

						@Override
						public String apply(QName input) {
							return input.toString();
						}
					}));

			valueList.add(PropertyResolver.getValues(instance, query, false));
		}

		return new DeepIterableKey(valueList);
	}

	@Override
	protected Instance merge(Collection<Instance> instances, TypeDefinition type,
			DeepIterableKey mergeKey, PropertiesMergeConfig mergeConfig) {
		if (instances.size() == 1) {
			// early exit if only one instance to merge
			return instances.iterator().next();
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
		// collect path roots
		for (List<QName> path : mergeConfig.keyProperties)
			rootNames.add(path.get(0));
		for (List<QName> path : mergeConfig.additionalProperties)
			rootNames.add(path.get(0));

		ArrayListMultimap<QName, Object[]> properties = ArrayListMultimap.create();
		for (Instance instance : instances)
			for (QName name : instance.getPropertyNames())
				properties.put(name, instance.getProperty(name));

		for (QName name : properties.keySet()) {
			if (rootNames.contains(name)
					|| (mergeConfig.autoDetect && allEqual(properties.get(name)))) {
				Object[] values = properties.get(name).get(0);
				for (Object value : values)
					result.addProperty(name, value);
			}
			else {
				for (Object[] values : properties.get(name))
					for (Object value : values)
						result.addProperty(name, value);
			}
		}

		// XXX what about metadata?!
		// XXX for now only retain IDs
		Set<Object> ids = new HashSet<Object>();
		for (Instance instance : instances) {
			List<Object> instanceIDs = instance.getMetaData(InstanceMetadata.METADATA_ID);
			for (Object id : instanceIDs) {
				ids.add(id);
			}
		}
		result.setMetaData(InstanceMetadata.METADATA_ID, ids.toArray());

		return result;
	}

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
}
