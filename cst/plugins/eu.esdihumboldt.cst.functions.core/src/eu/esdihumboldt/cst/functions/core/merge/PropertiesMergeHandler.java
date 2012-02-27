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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.Pair;

/**
 * Merge based on equal properties.
 * @author Simon Templer
 */
public class PropertiesMergeHandler extends AbstractMergeHandler<Pair<List<List<QName>>,List<List<QName>>>, DeepIterableKey> implements MergeFunction {
	//Pair<List<List<QName>>,List<List<QName>>> - first list key properties, second list additional merge properties
	@Override
	protected Pair<List<List<QName>>,List<List<QName>>> createMergeConfiguration(
			String transformationIdentifier,
			ListMultimap<String, String> transformationParameters,
			Map<String, String> executionParameters, TransformationLog log) throws TransformationException {
		if (transformationParameters == null 
				|| !transformationParameters.containsKey(PARAMETER_PROPERTY)) {
			throw new TransformationException("No merge property parameter defined");
		}
		
		List<List<QName>> properties = new ArrayList<List<QName>>();
		for (String property : transformationParameters.get(PARAMETER_PROPERTY)) {
			properties.add(PropertyResolver.getQNamesFromPath(property));
		}

		List<List<QName>> additionalProperties = new ArrayList<List<QName>>();
		if (transformationParameters.containsKey(PARAMETER_ADDITIONAL_PROPERTY)) {
			for (String property : transformationParameters.get(PARAMETER_ADDITIONAL_PROPERTY)) {
				additionalProperties.add(PropertyResolver.getQNamesFromPath(property));
			}
		}
		
		return new Pair<List<List<QName>>, List<List<QName>>>(properties, additionalProperties);
	}

	@Override
	protected DeepIterableKey getMergeKey(Instance instance,
			Pair<List<List<QName>>,List<List<QName>>> mergeConfig) {
		List<Object> valueList = new ArrayList<Object>(mergeConfig.getFirst().size());
		
		for (List<QName> propertyPath : mergeConfig.getFirst()) {
			String query = Joiner.on('.').join(
					Collections2.transform(propertyPath, new Function<QName, String>() {

				@Override
				public String apply(QName input) {
					return input.toString();
				}
			}));
			
			/*
			 * FIXME this will only work for non-instance values or instances 
			 * with a value (though not the whole instance will be compared)
			 */
			valueList.add(PropertyResolver.getValues(instance, query, true));
		}
		
		return new DeepIterableKey(valueList);
	}

	@Override
	protected Instance merge(Collection<Instance> instances,
			TypeDefinition type, DeepIterableKey mergeKey, 
			Pair<List<List<QName>>,List<List<QName>>> mergeConfig) {
		if (instances.size() == 1) {
			// early exit if only one instance to merge
			return instances.iterator().next();
		}
		
		MutableInstance result = getInstanceFactory().createInstance(type);
		
		/*
		 * FIXME This a first VERY basic implementation, where only the first
		 * item in each property path is regarded, and that whole tree is
		 * added only once (from the first instance).
		 * XXX This especially will be a problem, if a path contains a choice.
		 * XXX For more advanced stuff we need more advanced test cases.
		 */
		Set<QName> rootNames = new HashSet<QName>();
		// collect path roots
		for (List<QName> path : mergeConfig.getFirst())
			rootNames.add(path.get(0));
		for (List<QName> path : mergeConfig.getSecond())
			rootNames.add(path.get(0));
		
		for (Instance instance : instances) {
			for (QName name : instance.getPropertyNames()) {
				if (!rootNames.contains(name) || result.getProperty(name) == null) {
					Object[] values = instance.getProperty(name);
					if (values != null) {
						for (Object value : values) {
							result.addProperty(name, value);
						}
					}
				}
			}
		}
		
		return result;
	}

}
