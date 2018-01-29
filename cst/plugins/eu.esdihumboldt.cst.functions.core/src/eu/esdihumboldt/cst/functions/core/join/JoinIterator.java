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

package eu.esdihumboldt.cst.functions.core.join;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter;

/**
 * Iterator used by {@link JoinHandler}
 * 
 * @author Florian Esser
 */
class JoinIterator extends GenericResourceIteratorAdapter<InstanceReference, FamilyInstance> {

	private final InstanceCollection instances;
// type -> direct-parent
	private final int[] parent;
// TypeProp -> (Value -> Collection<Reference>)
	private final Map<PropertyEntityDefinition, Multimap<Object, InstanceReference>> index;
// ChildType -> (ParentType -> Collection<JoinCondition>)
	private final Map<Integer, Multimap<Integer, JoinCondition>> joinTable;

	private final ValueProcessor valueProcessor;

	protected JoinIterator(InstanceCollection instances,
			Collection<InstanceReference> startInstances, int[] parent,
			Map<PropertyEntityDefinition, Multimap<Object, InstanceReference>> index,
			Map<Integer, Multimap<Integer, JoinCondition>> joinTable,
			ValueProcessor valueProcessor) {
		super(startInstances.iterator());
		this.instances = instances;
		this.parent = parent;
		this.index = index;
		this.joinTable = joinTable;
		this.valueProcessor = valueProcessor;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter#convert(java.lang.Object)
	 */
	@Override
	protected FamilyInstance convert(InstanceReference next) {
		FamilyInstance base = new FamilyInstanceImpl(instances.getInstance(next));
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
				for (Map.Entry<Integer, JoinCondition> joinCondition : joinConditions.entries()) {
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
						Object keyValue = currentValue;
						if (valueProcessor != null) {
							keyValue = valueProcessor.processValue(currentValue,
									joinCondition.getValue().baseProperty);
						}
						matches.addAll(
								index.get(joinCondition.getValue().joinProperty).get(keyValue));
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
						FamilyInstance child;
						if (ref instanceof ResolvableInstanceReference) {
							child = new FamilyInstanceImpl(
									((ResolvableInstanceReference) ref).resolve());
						}
						else {
							child = new FamilyInstanceImpl(instances.getInstance(ref));
						}
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
