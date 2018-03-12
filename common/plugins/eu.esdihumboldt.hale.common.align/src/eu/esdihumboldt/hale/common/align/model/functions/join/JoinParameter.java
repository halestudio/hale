/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model.functions.join;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Information for a Join transformation.
 * 
 * @author Kai Schwierczek
 */
public class JoinParameter {

	/**
	 * Unmodifiable ordered list of the types to join.
	 */
	private final List<TypeEntityDefinition> types;

	/**
	 * Unmodifiable set of join conditions.
	 */
	private final Set<JoinCondition> conditions;

	/**
	 * Constructs a new join parameter descriptor.
	 * 
	 * @param types the list of join types, in order
	 * @param conditions the join conditions
	 */
	public JoinParameter(List<TypeEntityDefinition> types, Set<JoinCondition> conditions) {
		this.types = new ArrayList<>(types);
		this.conditions = new HashSet<>(conditions);
	}

	/**
	 * @return the types
	 */
	public List<TypeEntityDefinition> getTypes() {
		return Collections.unmodifiableList(types);
	}

	/**
	 * @return the conditions
	 */
	public Set<JoinCondition> getConditions() {
		return Collections.unmodifiableSet(conditions);
	}

	/**
	 * Checks whether this join parameter is valid.<br>
	 * <br>
	 * Valid means, that there has to be at least two types, with each type
	 * after the first having at least one join condition on previous types.
	 * 
	 * @return a error description or <code>null</code> if the parameter is
	 *         valid.
	 */
	public String validate() {
		return validate(false);
	}

	/**
	 * Checks whether this join parameter is valid.<br>
	 * <br>
	 * Valid means, that there has to be at least two types, with each type
	 * after the first having at least one join condition on previous types.
	 * 
	 * @param tryFix if it should be attempted to fix the configuration, the
	 *            user is expected to complete it
	 * 
	 * @return a error description or <code>null</code> if the parameter is
	 *         valid.
	 */
	public String validate(boolean tryFix) {
		// enough types?
		if (types.size() < 2) {
			return "Less than two types.";
		}

		// Check that each type is only in here once.
		Set<TypeDefinition> typeSet = new HashSet<>();
		for (TypeEntityDefinition type : types) {
			if (typeSet.contains(type.getDefinition())) {
				return "Same base type is used twice.";
			}
			else {
				typeSet.add(type.getDefinition());
			}
		}

		// direct parent map
		int[] parent = new int[types.size()];
		// marker for found conditions for each type
		boolean[] conditionFound = new boolean[types.size()];
		// use sorted conditions (by join type)
		List<JoinCondition> sortedConditions = new ArrayList<>(conditions);
		Collections.sort(sortedConditions, new Comparator<JoinCondition>() {

			@Override
			public int compare(JoinCondition o1, JoinCondition o2) {
				TypeEntityDefinition o1Type = AlignmentUtil.getTypeEntity(o1.baseProperty);
				TypeEntityDefinition o2Type = AlignmentUtil.getTypeEntity(o2.baseProperty);
				return types.indexOf(o1Type) - types.indexOf(o2Type);
			}
		});

		// check types of each condition
		for (JoinCondition condition : sortedConditions) {
			TypeEntityDefinition baseType = AlignmentUtil.getTypeEntity(condition.baseProperty);
			TypeEntityDefinition joinType = AlignmentUtil.getTypeEntity(condition.joinProperty);
			int baseIndex = types.indexOf(baseType);
			int joinIndex = types.indexOf(joinType);
			// types have to exist, and join has to be after base
			if (baseIndex == -1 || joinIndex == -1) {
				if (tryFix) {
					// remove wrong condition
					this.conditions.remove(condition);
				}
				else {
					return "Property references no specified type.";
				}
			}
			if (joinIndex <= baseIndex) {
				if (tryFix) {
					// remove wrong condition
					this.conditions.remove(condition);
				}
				else {
					return "Invalid condition for type order.";
				}
			}

			if (joinIndex >= 0) {
				conditionFound[joinIndex] = true;

				// sorted conditions allow this dependsOn-check
				if (parent[joinIndex] < baseIndex) {
					if (!dependsOn(baseIndex, parent[joinIndex], parent)) {
						if (tryFix) {
							// ignore
						}
						else {
							return "A join type depends on two types which do not depend on each other.";
						}
					}
					parent[joinIndex] = baseIndex;
				}
			}
		}

		// check whether each type (except the first) has a join condition
		for (int i = 1; i < conditionFound.length; i++) {
			if (!conditionFound[i] && !tryFix) {
				return "A joined type does not have any join conditions.";
			}
		}

		return null;
	}

	// Checks whether the first type depends on the second type.
	private boolean dependsOn(int highType, int lowType, int[] parent) {
		while (highType > lowType)
			highType = parent[highType];

		return highType == lowType;
	}

	/**
	 * Represents a single join condition.
	 */
	public static class JoinCondition {

		/**
		 * The property of the base type.
		 */
		public final PropertyEntityDefinition baseProperty;
		/**
		 * The property of the type to join.
		 */
		public final PropertyEntityDefinition joinProperty;

		/**
		 * Constructs a join condition to join the type of
		 * <code>joinProperty</code> if the condition
		 * <code>joinProperty = baseProperty</code> matches.
		 * 
		 * @param baseProperty the property of a base type
		 * @param joinProperty the property of the type to join
		 */
		public JoinCondition(PropertyEntityDefinition baseProperty,
				PropertyEntityDefinition joinProperty) {
			this.baseProperty = baseProperty;
			this.joinProperty = joinProperty;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((baseProperty == null) ? 0 : baseProperty.hashCode());
			result = prime * result + ((joinProperty == null) ? 0 : joinProperty.hashCode());
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JoinCondition other = (JoinCondition) obj;
			if (baseProperty == null) {
				if (other.baseProperty != null)
					return false;
			}
			else if (!baseProperty.equals(other.baseProperty))
				return false;
			if (joinProperty == null) {
				if (other.joinProperty != null)
					return false;
			}
			else if (!joinProperty.equals(other.joinProperty))
				return false;
			return true;
		}
	}
}
