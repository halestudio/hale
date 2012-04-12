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

package eu.esdihumboldt.cst.functions.core.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.util.Pair;

/**
 * Join based on equal properties.
 * 
 * @author Kai Schwierczek
 */
public class JoinHandler implements InstanceHandler<TransformationEngine>, JoinFunction  {
	// For now no support to join the same type on some condition.
	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler#partitionInstances(eu.esdihumboldt.hale.common.instance.model.InstanceCollection, java.lang.String, eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine, com.google.common.collect.ListMultimap, java.util.Map, eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	public ResourceIterator<Collection<Instance>> partitionInstances(InstanceCollection instances,
			String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, String> transformationParameters, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException {
		if (transformationParameters == null
				|| !transformationParameters.containsKey(PARAMETER_JOIN)
				|| transformationParameters.get(PARAMETER_JOIN).isEmpty()) {
			throw new TransformationException("No join parameter defined");
		}

		// idea for join parameter format: type1 ... typeN  type1,prop1=type2,prop2 ... typeN-1,propN-1=typeN,propN
		// so: first types in join order URLEncoded, space separated, then two spaces,.
		// second space separated pairs of '=' separated join conditions as "<type>,<property>", 
		// also in order: type on the left side has to be type in front of the type on the right side.

		// first collect relevant properties per type to build an index of those
		String joinParameter = transformationParameters.get(PARAMETER_JOIN).get(0);
		String[] typesAndProps = joinParameter.split("  ", -1);

		// has two be two parts
		if (typesAndProps.length != 2)
			throw new TransformationException("Join parameter invalid.");
		List<String> types = Arrays.asList(typesAndProps[0].split(" "));

		// join with less than two types is senseless
		if (types.size() < 2)
			throw new TransformationException("Join parameter invalid.");

		// build join and property maps
		//  Type2            Type1          Prop1   Prop2
		Map<Integer, Multimap<Integer, Pair<String, String>>> joinTable = new HashMap<Integer, Multimap<Integer,Pair<String,String>>>(types.size() * 2);
		// table to quickly find the properties over which to build an index
		//       Type    Property
		Multimap<String, String> propertyTable = HashMultimap.create();
		for (String propEquality : typesAndProps[1].split(" ")) {
			String[] equalSides = propEquality.split("=");
			
			if (equalSides.length != 2)
				throw new TransformationException("Join parameter invalid.");
			
			String[] typePropOne = equalSides[0].split(",");
			String[] typePropTwo = equalSides[1].split(",");
			
			// some checks...
			if (typePropOne.length != 2 || typePropTwo.length != 2
					|| types.indexOf(typePropOne[0]) == -1 || types.indexOf(typePropTwo[0]) == -1
					|| types.indexOf(typePropTwo[0]) <= types.indexOf(typePropOne[0]))
				throw new TransformationException("Join parameter invalid.");

			int typeOneIndex = types.indexOf(typePropOne[0]);
			int typeTwoIndex = types.indexOf(typePropTwo[0]);
			if (joinTable.get(typeTwoIndex) == null)
				joinTable.put(typeTwoIndex, ArrayListMultimap.<Integer, Pair<String,String>>create(3, 3));
			joinTable.get(typeTwoIndex).put(typeOneIndex, new Pair<String, String>(typePropOne[1], typePropTwo[1]));

			// build index over second type/property
			propertyTable.put(typePropTwo[0], typePropTwo[1]);
		}

		// each type except first has to have a join condition. (or not? Cartesian product?)
		if (joinTable.keySet().size() != types.size() - 1)
			throw new TransformationException("Join parameter invalid.");

		// index over type/properties, their values and the instance reference of instances with the specific values
		//  TypeProp         Value   Reference
		Map<String, Multimap<Object, InstanceReference>> index = new HashMap<String, Multimap<Object, InstanceReference>>();
		for (Map.Entry<String, String> typeProp : propertyTable.entries()) {
			index.put(typeProp.getKey() + "." + typeProp.getValue(),
					ArrayListMultimap.<Object, InstanceReference>create());
		}

		// remember instances of first type to start join afterwards
		Collection<InstanceReference> startInstances = new LinkedList<InstanceReference>();

		// iterate once over all instances
		ResourceIterator<Instance> iterator = instances.iterator();
		try {
			while (iterator.hasNext()) {
				Instance next = iterator.next();

				// remember instances of first type
				// TODO use something else than display name (QName)
				String type = next.getDefinition().getDisplayName();
				if (type.equals(types.get(0)))
					startInstances.add(instances.getReference(next));

				// fill index over needed properties
				for (String property : propertyTable.get(type)) {
					// XXX what about null? for now ignore null values
					// XXX how to treat multiple values? must all be equal (in order?) or only one?
					Collection<Object> values = PropertyResolver.getValues(next, property, true);
					if (values != null && !values.isEmpty()) {
						// XXX take only first value for now
						String typeProp = type + "." + property;
						index.get(typeProp).put(values.iterator().next(), instances.getReference(next));
					}
				}
			}
		} finally {
			iterator.close();
		}

		return new JoinIterator(instances, startInstances, types, index, joinTable);
	}

	private class JoinIterator implements ResourceIterator<Collection<Instance>> {
		private final InstanceCollection instances;
		private final List<String> types;
		//                TypeProp         Value   Reference
		private final Map<String, Multimap<Object, InstanceReference>> index;
		//                Type2            Type1          Prop1   Prop2
		private final Map<Integer, Multimap<Integer, Pair<String, String>>> joinTable;
	
		private LinkedList<Iterator<InstanceReference>> currentIterators;
		private List<Instance> currentInstances;

		// next entry, selected through hasNext...
		private Collection<Instance> next;

		protected JoinIterator(InstanceCollection instances, Collection<InstanceReference> startInstances,
				List<String> types, Map<String, Multimap<Object, InstanceReference>> index,
				Map<Integer, Multimap<Integer, Pair<String, String>>> joinTable) {
			this.instances = instances;
			this.types = types;
			this.index = index;
			this.joinTable = joinTable;

			currentIterators = new LinkedList<Iterator<InstanceReference>>();
			currentInstances = new ArrayList<Instance>(types.size());
			currentIterators.add(startInstances.iterator());

			next = null;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			if (next == null) {
				try {
					next = next();
				} catch (NoSuchElementException nsee) {
					return false;
				}
			}
			return true;
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Collection<Instance> next() {
			// first check whether the result was fetched by hasNext already
			if (next != null) {
				Collection<Instance> result = next;
				next = null;
				return result;
			}

			findNext();

			// return result
			return new ArrayList<Instance>(currentInstances);
		}

		/**
		 * Searches the next result of this join operation.<br>
		 * The result will be in the currentInstances List.
		 * 
		 * @throws NoSuchElementException if no result is available
		 */
		private void findNext() throws NoSuchElementException {
			// remove last Instance from currentInstance if this isn't the first next call
			if (!currentInstances.isEmpty())
				currentInstances.remove(currentInstances.size() - 1);

			// start with highest depth iterator
			Iterator<InstanceReference> currentIterator = currentIterators.getLast();

			while (true) {
				while (!currentIterator.hasNext()) {
					// this iterator is done, remove it
					currentIterators.removeLast();
					// retreat
					currentIterator = currentIterators.getLast();
					currentInstances.remove(currentInstances.size() - 1);
				}

				// add next possibility to currentInstances
				currentInstances.add(instances.getInstance(currentIterator.next()));

				// are we done?
				if (currentInstances.size() == types.size())
					return;

				// get join condition for next type
				//       Type1          Prop1   Prop2                                  Type2
				Multimap<Integer, Pair<String, String>> joinConditions = joinTable.get(currentInstances.size());
				// collect intersection of conditions      null marks beginning in contrast to empty
				Set<InstanceReference> possibleInstances = null;
				//             Type1         Prop1   Prop2
				for (Map.Entry<Integer, Pair<String, String>> joinCondition : joinConditions.entries()) {
					Collection<Object> currentValues = PropertyResolver.getValues(currentInstances.get(joinCondition.getKey()), joinCondition.getValue().getFirst());

					// XXX no value? for now that is not a match in every case.
					if (currentValues == null || currentValues.isEmpty()) {
						possibleInstances = Collections.emptySet();
						break;
					}

					// XXX take first value for now
					Object currentValue = currentValues.iterator().next();
					Collection<InstanceReference> matches = index.get(types.get(currentInstances.size()) + "." + joinCondition.getValue().getSecond()).get(currentValue);
					if (possibleInstances == null)
						possibleInstances = new HashSet<InstanceReference>(matches);
					else {
						// intersect
						// XXX put matches in HashSet for fast contains? worth it?
						Iterator<InstanceReference> iter = possibleInstances.iterator();
						while (iter.hasNext()) {
							InstanceReference ref = iter.next();
							if (!matches.contains(ref))
								iter.remove();
						}
					}

					// break is set is empty
					if (possibleInstances.isEmpty())
						break;
				}

				// found possibilities?
				if (possibleInstances != null && !possibleInstances.isEmpty()) {
					// add iterator over found possibilities
					currentIterator = possibleInstances.iterator();
					// advance
					currentIterators.add(currentIterator);
				} else {
					// remove last instance but keep iterator
					currentInstances.remove(currentInstances.size() - 1);
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

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.ResourceIterator#close()
		 */
		@Override
		public void close() {
			// do nothing?
		}
	}
}
