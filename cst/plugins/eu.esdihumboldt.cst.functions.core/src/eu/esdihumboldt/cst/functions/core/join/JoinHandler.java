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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter;
import eu.esdihumboldt.util.Pair;

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

		// idea for join parameter format: type1 ...
		// typeN::type1,prop1=type2,prop2 ... typeN-1,propN-1=typeN,propN
		// so: first types in join order - QNames, space separated (focus on
		// left types happens!), then '::'
		// second space separated pairs of '=' separated join conditions as
		// "<type>,<property>",
		// also in order: type on the left side has to be type in front of the
		// type on the right side.

		// first collect relevant properties per type to build an index of those
		String joinParameter = transformationParameters.get(PARAMETER_JOIN).get(0).as(String.class);
		String[] typesAndProps = joinParameter.split("::", -1);

		// has two be two parts
		if (typesAndProps.length != 2)
			throw new TransformationException("Join parameter invalid.");
		String[] typesRaw = typesAndProps[0].split(" ");
		// List of types with their QNames, and the highest type they depend on.
		List<Pair<QName, Integer>> types = new ArrayList<Pair<QName, Integer>>(typesRaw.length);
		for (String name : typesAndProps[0].split(" "))
			types.add(new Pair<QName, Integer>(QName.valueOf(name), 0));

		// join with less than two types is senseless
		if (types.size() < 2)
			throw new TransformationException("Join parameter invalid.");

		// build join and property maps
		// Type2 Type1 Prop1 Prop2
		Map<Integer, Multimap<Integer, Pair<String, String>>> joinTable = new HashMap<Integer, Multimap<Integer, Pair<String, String>>>(
				types.size() * 2);
		// table to quickly find the properties over which to build an index
		// Type Property
		Multimap<String, String> propertyTable = HashMultimap.create();
		for (String propEquality : typesAndProps[1].split(" ")) {
			String[] equalSides = propEquality.split("=");

			if (equalSides.length != 2)
				throw new TransformationException("Join parameter invalid.");

			String[] typePropOne = equalSides[0].split(",");
			String[] typePropTwo = equalSides[1].split(",");

			int typeOneIndex = -1;
			int typeTwoIndex = -1;
			for (int i = 0; i < types.size(); i++)
				if (types.get(i).getFirst().toString().equals(typePropOne[0])) {
					typeOneIndex = i;
					break;
				}
			for (int i = 0; i < types.size(); i++)
				if (types.get(i).getFirst().toString().equals(typePropTwo[0])) {
					typeTwoIndex = i;
					break;
				}

			// some checks...
			if (typePropOne.length != 2 || typePropTwo.length != 2 || typeOneIndex == -1
					|| typeTwoIndex == -1 || typeTwoIndex <= typeOneIndex)
				throw new TransformationException("Join parameter invalid.");

			// add it to join table
			if (joinTable.get(typeTwoIndex) == null)
				joinTable.put(typeTwoIndex,
						ArrayListMultimap.<Integer, Pair<String, String>> create(3, 3));
			joinTable.get(typeTwoIndex).put(typeOneIndex,
					new Pair<String, String>(typePropOne[1], typePropTwo[1]));

			// update highest type if necessary
			if (types.get(typeTwoIndex).getSecond() < typeOneIndex) {
				if (!dependsOn(typeOneIndex, types.get(typeTwoIndex).getSecond(), types))
					throw new TransformationException(
							"Dependency on two different types which do not depend on each other is not supported.");
				types.set(typeTwoIndex, new Pair<QName, Integer>(
						types.get(typeTwoIndex).getFirst(), typeOneIndex));
			}

			// build index over second type/property
			propertyTable.put(typePropTwo[0], typePropTwo[1]);
		}

		// each type except first has to have a join condition. (or not?
		// Cartesian product?)
		if (joinTable.keySet().size() != types.size() - 1)
			throw new TransformationException("Join parameter invalid.");

		// index over type/properties, their values and the instance reference
		// of instances with the specific values
		// TypeProp Value Reference
		Map<String, Multimap<Object, InstanceReference>> index = new HashMap<String, Multimap<Object, InstanceReference>>();
		for (Map.Entry<String, String> typeProp : propertyTable.entries()) {
			index.put(typeProp.getKey() + "." + typeProp.getValue(),
					ArrayListMultimap.<Object, InstanceReference> create());
		}

		// remember instances of first type to start join afterwards
		Collection<InstanceReference> startInstances = new LinkedList<InstanceReference>();

		// iterate once over all instances
		ResourceIterator<Instance> iterator = instances.iterator();
		try {
			while (iterator.hasNext()) {
				Instance next = iterator.next();

				// remember instances of first type
				String type = next.getDefinition().getName().toString();
				if (type.equals(types.get(0).getFirst().toString()))
					startInstances.add(instances.getReference(next));

				// fill index over needed properties
				for (String property : propertyTable.get(type)) {
					// XXX what about null? for now ignore null values
					// XXX how to treat multiple values? must all be equal (in
					// order?) or only one?
					Collection<Object> values = PropertyResolver.getValues(next, property, true);
					if (values != null && !values.isEmpty()) {
						// XXX take only first value for now
						String typeProp = type + "." + property;
						index.get(typeProp).put(values.iterator().next(),
								instances.getReference(next));
					}
				}
			}
		} finally {
			iterator.close();
		}

		return new JoinIterator(instances, startInstances, types, index, joinTable);
	}

	// Checks whether the first type depends on the second type in the given
	// type list.
	private boolean dependsOn(int type1, int type2, List<Pair<QName, Integer>> types) {
		if (type1 == type2)
			return true;

		while (type1 > type2) {
			type1 = types.get(type1).getSecond();
			if (type1 == type2)
				return true;
		}

		return false;
	}

	private class JoinIterator extends
			GenericResourceIteratorAdapter<InstanceReference, FamilyInstance> {

		private final InstanceCollection instances;
		private final List<Pair<QName, Integer>> types;
		// TypeProp Value Reference
		private final Map<String, Multimap<Object, InstanceReference>> index;
		// Type2 Type1 Prop1 Prop2
		private final Map<Integer, Multimap<Integer, Pair<String, String>>> joinTable;

		protected JoinIterator(InstanceCollection instances,
				Collection<InstanceReference> startInstances, List<Pair<QName, Integer>> types,
				Map<String, Multimap<Object, InstanceReference>> index,
				Map<Integer, Multimap<Integer, Pair<String, String>>> joinTable) {
			super(startInstances.iterator());
			this.instances = instances;
			this.types = types;
			this.index = index;
			this.joinTable = joinTable;
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter#convert(java.lang.Object)
		 */
		@Override
		protected FamilyInstance convert(InstanceReference next) {
			FamilyInstance base = new FamilyInstanceImpl(instances.getInstance(next));
			FamilyInstance[] currentInstances = new FamilyInstance[types.size()];
			currentInstances[0] = base;

			join(currentInstances, 0);

			return base;
		}

		// Joins all direct children of the given type to currentInstances.
		private void join(FamilyInstance[] currentInstances, int currentType) {
			// Join all types that are direct children of the last type.
			for (int i = currentType + 1; i < types.size(); i++) {
				if (types.get(i).getSecond() == currentType) {
					// Get join condition for the direct child type.
					// Type1 Prop1 Prop2 of Type2
					Multimap<Integer, Pair<String, String>> joinConditions = joinTable.get(i);
					// Collect intersection of conditions. null marks beginning
					// in contrast to an empty set.
					Set<InstanceReference> possibleInstances = null;
					// Type1 Prop1 Prop2
					for (Map.Entry<Integer, Pair<String, String>> joinCondition : joinConditions
							.entries()) {
						Collection<Object> currentValues = PropertyResolver.getValues(
								currentInstances[joinCondition.getKey()], joinCondition.getValue()
										.getFirst());

						if (currentValues == null) {
							possibleInstances = Collections.emptySet();
							break;
						}

						// Allow targets with any of the property values.
						HashSet<InstanceReference> matches = new HashSet<InstanceReference>();
						for (Object currentValue : currentValues) {
							// XXX Would someone join over a Group/Instance?
							matches.addAll(index.get(
									types.get(i).getFirst().toString() + "."
											+ joinCondition.getValue().getSecond()).get(
									currentValue));
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
							FamilyInstance child = new FamilyInstanceImpl(
									instances.getInstance(ref));
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
