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

package eu.esdihumboldt.cst.functions.core.join;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.cst.functions.core.join.JoinUtil.JoinDefinition;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Join based on equal properties.
 * 
 * @author Kai Schwierczek
 */
public class JoinHandler implements InstanceHandler<TransformationEngine>, JoinFunction {

	private final ValueProcessor valueProcessor = new DefaultValueProcessor();

	// For now no support for using the same type more than once in a join.
	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler#partitionInstances(eu.esdihumboldt.hale.common.instance.model.InstanceCollection,
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
		if (transformationParameters == null
				|| !transformationParameters.containsKey(PARAMETER_JOIN)
				|| transformationParameters.get(PARAMETER_JOIN).isEmpty()) {
			throw new TransformationException("No join parameter defined");
		}

		JoinParameter joinParameter = transformationParameters.get(PARAMETER_JOIN).get(0)
				.as(JoinParameter.class);

		String validation = joinParameter.validate();
		if (validation != null)
			throw new TransformationException("Join parameter invalid: " + validation);

		List<TypeEntityDefinition> types = joinParameter.getTypes();

		JoinDefinition joinDefinition = JoinUtil.getJoinDefinition(joinParameter);

		// JoinProperty -> (Value -> Collection<Reference>)
		Map<PropertyEntityDefinition, Multimap<Object, InstanceReference>> index = new HashMap<>();
		for (PropertyEntityDefinition property : joinDefinition.properties.values())
			index.put(property, ArrayListMultimap.<Object, InstanceReference> create());

		// remember instances of first type to start join afterwards
		Collection<InstanceReference> startInstances = new LinkedList<InstanceReference>();

		// iterate once over all instances
		ResourceIterator<Instance> iterator = instances.iterator();
		try {
			while (iterator.hasNext()) {
				Instance next = iterator.next();

				// remember instances of first type
				if (next.getDefinition().equals(types.get(0).getDefinition())) {
					startInstances.add(instances.getReference(next));
				}

				// fill index over needed properties
				for (PropertyEntityDefinition property : joinDefinition.properties
						.get(next.getDefinition())) {
					// XXX what about null? for now ignore null values
					// XXX how to treat multiple values? must all be equal (in
					// order?) or only one?
					Collection<Object> values = AlignmentUtil.getValues(next, property, true);
					if (values != null && !values.isEmpty()) {
						// XXX take only first value for now
						index.get(property).put(
								valueProcessor.processValue(values.iterator().next(), property),
								instances.getReference(next));
					}
				}
			}
		} finally {
			iterator.close();
		}

		return new JoinIterator(instances, startInstances, joinDefinition.directParent, index,
				joinDefinition.joinTable, valueProcessor);
	}
}