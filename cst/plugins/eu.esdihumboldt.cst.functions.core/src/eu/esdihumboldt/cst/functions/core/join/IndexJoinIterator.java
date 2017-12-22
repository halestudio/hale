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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.google.common.collect.Multimap;

import eu.esdihumboldt.cst.functions.core.join.JoinUtil.JoinDefinition;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.instance.index.IndexedPropertyValue;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexService;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter;

/**
 * Iterator used by {@link IndexJoinHandler}
 * 
 * @author Florian Esser
 */
class IndexJoinIterator
		extends GenericResourceIteratorAdapter<ResolvableInstanceReference, FamilyInstance> {

	private final JoinDefinition joinDefinition;
	private final InstanceIndexService index;

	protected IndexJoinIterator(Collection<ResolvableInstanceReference> startInstances,
			JoinDefinition joinDefinition, InstanceIndexService index) {
		super(startInstances.iterator());
		this.joinDefinition = joinDefinition;
		this.index = index;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter#convert(java.lang.Object)
	 */
	@Override
	protected FamilyInstance convert(ResolvableInstanceReference next) {
		FamilyInstance base = new FamilyInstanceImpl(next.resolve());
		base.getId();
		FamilyInstance[] currentInstances = new FamilyInstance[joinDefinition.directParent.length];
		currentInstances[0] = base;

		join(currentInstances, 0);

		return base;
	}

	/**
	 * Joins all direct children of the given type to currentInstances.
	 */
	@SuppressWarnings("javadoc")
	private void join(FamilyInstance[] currentInstances, int currentType) {
		// Join all types that are direct children of the last type.
		for (int i = currentType + 1; i < joinDefinition.directParent.length; i++) {
			if (joinDefinition.directParent[i] == currentType) {
				// Get join condition for the direct child type.
				Multimap<Integer, JoinCondition> joinConditions = joinDefinition.joinTable.get(i);

				// Collect intersection of conditions. null marks beginning
				// in contrast to an empty set.
				Set<ResolvableInstanceReference> possibleInstances = null;

				// ParentType -> JoinConditions
				for (Map.Entry<Integer, JoinCondition> joinCondition : joinConditions.entries()) {
					PropertyEntityDefinition baseProp = joinCondition.getValue().baseProperty;
					QName baseTypeName = baseProp.getType().getName();
					List<QName> basePropertyPath = baseProp.getPropertyPath().stream()
							.map(pp -> pp.getChild().getName()).collect(Collectors.toList());

					PropertyEntityDefinition joinProp = joinCondition.getValue().joinProperty;
					QName joinTypeName = joinProp.getType().getName();
					List<QName> joinPropertyPath = joinProp.getPropertyPath().stream()
							.map(pp -> pp.getChild().getName()).collect(Collectors.toList());

					List<IndexedPropertyValue> currentValues = index.getInstancePropertyValues(
							baseTypeName, basePropertyPath,
							currentInstances[joinCondition.getKey()].getId());

					if (currentValues == null || currentValues.isEmpty()) {
						possibleInstances = Collections.emptySet();
						break;
					}

					HashSet<ResolvableInstanceReference> matches = new HashSet<ResolvableInstanceReference>();
					for (IndexedPropertyValue currentValue : currentValues) {
						if (currentValue.getValues() == null
								|| currentValue.getValues().isEmpty()) {
							continue;
						}

						// Find instances that have the current property value
						Collection<ResolvableInstanceReference> instancesWithValues = index
								.getInstancesByValue(joinTypeName, joinPropertyPath,
										currentValue.getValues());
						matches.addAll(instancesWithValues);
					}

					if (possibleInstances == null) {
						possibleInstances = matches;
					}
					else {
						// Remove candidates that don't have the current
						// property value
						Iterator<ResolvableInstanceReference> it = possibleInstances.iterator();
						while (it.hasNext()) {
							ResolvableInstanceReference cand = it.next();
							if (!matches.contains(cand)) {
								it.remove();
							}
						}
					}

					if (possibleInstances.isEmpty()) {
						break;
					}
				}

				if (possibleInstances != null && !possibleInstances.isEmpty()) {
					FamilyInstance parent = currentInstances[currentType];
					for (ResolvableInstanceReference ref : possibleInstances) {
						FamilyInstance child;
						child = new FamilyInstanceImpl(ref.resolve());
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
