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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceDelegate;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;

/**
 * Join based on equal properties.
 * 
 * @author Kai Schwierczek
 */
public class JoinHandler implements InstanceHandler<TransformationEngine>, JoinFunction {

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

		List<TypeEntityDefinition> types = joinParameter.types;
		// ChildType -> DirectParentType
		int[] directParent = new int[joinParameter.types.size()];
		// ChildType -> (ParentType -> Collection<JoinCondition>)
		Map<Integer, Multimap<Integer, JoinCondition>> joinTable = new HashMap<>();
		// all joined properties
		Multimap<TypeDefinition, PropertyEntityDefinition> properties = HashMultimap.create();

		for (JoinCondition condition : joinParameter.conditions) {
			int baseTypeIndex = types.indexOf(AlignmentUtil.getTypeEntity(condition.baseProperty));
			int joinTypeIndex = types.indexOf(AlignmentUtil.getTypeEntity(condition.joinProperty));
			Multimap<Integer, JoinCondition> typeTable = joinTable.get(joinTypeIndex);
			if (typeTable == null) {
				typeTable = ArrayListMultimap.create(2, 2);
				joinTable.put(joinTypeIndex, typeTable);
			}
			typeTable.put(baseTypeIndex, condition);

			// update highest type if necessary
			if (directParent[joinTypeIndex] < baseTypeIndex)
				directParent[joinTypeIndex] = baseTypeIndex;

			properties.put(condition.joinProperty.getType(), condition.joinProperty);
		}

		// JoinProperty -> (Value -> Collection<Reference>)
		Map<PropertyEntityDefinition, Multimap<Object, InstanceReference>> index = new HashMap<>();
		for (PropertyEntityDefinition property : properties.values())
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
				for (PropertyEntityDefinition property : properties.get(next.getDefinition())) {
					// XXX what about null? for now ignore null values
					// XXX how to treat multiple values? must all be equal (in
					// order?) or only one?
					Collection<Object> values = AlignmentUtil.getValues(next, property, true);
					if (values != null && !values.isEmpty()) {
						// XXX take only first value for now
						index.get(property).put(processValue(values.iterator().next(), property),
								instances.getReference(next));
					}
				}
			}
		} finally {
			iterator.close();
		}

		return new JoinIterator(instances, startInstances, directParent, index, joinTable);
	}

	/**
	 * Process a value of a property in a join condition before using it with
	 * the index.
	 * 
	 * @param value the value
	 * @param property the entity definition the value is associated to
	 * @return the processed value, possibly wrapped or replaced through a
	 *         different representation
	 */
	protected Object processValue(Object value, PropertyEntityDefinition property) {
		// extract the identifier from a reference
		value = property.getDefinition().getConstraint(Reference.class).extractId(value);

		/*
		 * This is done so values will be classified as equal even if they are
		 * of different types, e.g. Long and Integer or Integer and String.
		 */

		/*
		 * Use string representation for numbers.
		 */
		if (value instanceof Number) {
			if (value instanceof BigInteger || value instanceof Long || value instanceof Integer
					|| value instanceof Byte || value instanceof Short) {
				// use string representation for integer numbers
				value = value.toString();
			}
			else if (value instanceof BigDecimal) {
				BigDecimal v = (BigDecimal) value;
				if (v.scale() <= 0) {
					// use string representation for integer big decimal
					value = v.toBigInteger().toString();
				}
			}
		}

		/*
		 * Use string representation for URIs and URLs.
		 */
		if (value instanceof URI || value instanceof URL) {
			value = value.toString();
		}

		return value;
	}

	private class JoinIterator
			extends GenericResourceIteratorAdapter<InstanceReference, FamilyInstance> {

		private final InstanceCollection instances;
		// type -> direct-parent
		private final int[] parent;
		// TypeProp -> (Value -> Collection<Reference>)
		private final Map<PropertyEntityDefinition, Multimap<Object, InstanceReference>> index;
		// ChildType -> (ParentType -> Collection<JoinCondition>)
		private final Map<Integer, Multimap<Integer, JoinCondition>> joinTable;

		protected JoinIterator(InstanceCollection instances,
				Collection<InstanceReference> startInstances, int[] parent,
				Map<PropertyEntityDefinition, Multimap<Object, InstanceReference>> index,
				Map<Integer, Multimap<Integer, JoinCondition>> joinTable) {
			super(startInstances.iterator());
			this.instances = instances;
			this.parent = parent;
			this.index = index;
			this.joinTable = joinTable;
		}

		/**
		 * Create a family instance from an instance reference.
		 * 
		 * @param ref the instance reference
		 * @return the family instance created from the reference
		 */
		protected FamilyInstance createFamilyInstance(InstanceReference ref) {
//			return new FamilyInstanceImpl(instances.getInstance(ref));
			return new FamilyInstanceDelegate(ref, instances);
		}

		@Override
		protected FamilyInstance convert(InstanceReference next) {
			FamilyInstance base = createFamilyInstance(next);
			FamilyInstance[] currentInstances = new FamilyInstance[parent.length];
			currentInstances[0] = base;

			join(currentInstances, 0);

			return base;
		}

		// Joins all direct children of the given type to currentInstances.
		private void join(FamilyInstance[] currentInstances, int currentType) {
			// Join all types that are direct children of the last type.
			for (int i = currentType + 1; i < parent.length; i++) {
				if (parent[i] == currentType) {
					// Get join condition for the direct child type.
					Multimap<Integer, JoinCondition> joinConditions = joinTable.get(i);
					// Collect intersection of conditions. null marks beginning
					// in contrast to an empty set.
					Set<InstanceReference> possibleInstances = null;
					// ParentType -> JoinConditions
					for (Map.Entry<Integer, JoinCondition> joinCondition : joinConditions
							.entries()) {
						Collection<Object> currentValues = AlignmentUtil.getValues(
								currentInstances[joinCondition.getKey()],
								joinCondition.getValue().baseProperty, true);

						if (currentValues == null) {
							possibleInstances = Collections.emptySet();
							break;
						}

						// Allow targets with any of the property values.
						HashSet<InstanceReference> matches = new HashSet<InstanceReference>();
						for (Object currentValue : currentValues) {
							matches.addAll(index.get(joinCondition.getValue().joinProperty)
									.get(processValue(currentValue,
											joinCondition.getValue().baseProperty)));
						}
						if (possibleInstances == null)
							possibleInstances = matches;
						else {
							// Intersect!
							Iterator<InstanceReference> iter = possibleInstances.iterator();
							while (iter.hasNext()) {
								InstanceReference ref = iter.next();
								if (!matches.contains(ref))
									iter.remove();
							}
						}

						// Break if set is empty.
						if (possibleInstances.isEmpty())
							break;
					}

					if (possibleInstances != null && !possibleInstances.isEmpty()) {
						FamilyInstance parent = currentInstances[currentType];
						for (InstanceReference ref : possibleInstances) {
							FamilyInstance child = createFamilyInstance(ref);
							parent.addChild(child);
							currentInstances[i] = child;
							join(currentInstances, i);
						}
						currentInstances[i] = null;
					}
				}
			}
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
