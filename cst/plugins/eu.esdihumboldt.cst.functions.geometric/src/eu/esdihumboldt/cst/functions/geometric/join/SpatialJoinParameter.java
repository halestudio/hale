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

package eu.esdihumboldt.cst.functions.geometric.join;

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
 * Parameter for a spatial join transformation.
 * 
 * @author Florian Esser
 */
public class SpatialJoinParameter {

	/**
	 * Unmodifiable ordered list of the types to join.
	 */
	public final List<TypeEntityDefinition> types;

	/**
	 * Unmodifiable set of join conditions.
	 */
	public final Set<SpatialJoinCondition> conditions;

	/**
	 * Constructs a new join parameter descriptor.
	 * 
	 * @param types the list of join types, in order
	 * @param conditions the join conditions
	 */
	public SpatialJoinParameter(List<TypeEntityDefinition> types,
			Set<SpatialJoinCondition> conditions) {
		this.types = Collections.unmodifiableList(new ArrayList<>(types));
		this.conditions = Collections.unmodifiableSet(new HashSet<>(conditions));
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
		// enough types?
		if (types.size() < 2)
			return "Less than two types.";

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
		List<SpatialJoinCondition> sortedConditions = new ArrayList<>(conditions);
		Collections.sort(sortedConditions, new Comparator<SpatialJoinCondition>() {

			@Override
			public int compare(SpatialJoinCondition o1, SpatialJoinCondition o2) {
				TypeEntityDefinition o1Type = AlignmentUtil.getTypeEntity(o1.baseProperty);
				TypeEntityDefinition o2Type = AlignmentUtil.getTypeEntity(o2.baseProperty);
				return types.indexOf(o1Type) - types.indexOf(o2Type);
			}
		});

		// check types of each condition
		for (SpatialJoinCondition condition : sortedConditions) {
			TypeEntityDefinition baseType = AlignmentUtil.getTypeEntity(condition.baseProperty);
			TypeEntityDefinition joinType = AlignmentUtil.getTypeEntity(condition.joinProperty);
			int baseIndex = types.indexOf(baseType);
			int joinIndex = types.indexOf(joinType);
			// types have to exist, and join has to be after base
			if (baseIndex == -1 || joinIndex == -1) {
				return "Property references no specified type.";
			}
			if (joinIndex <= baseIndex) {
				return "Invalid condition for type order.";
			}

			conditionFound[joinIndex] = true;

			// sorted conditions allow this dependsOn-check
			if (parent[joinIndex] < baseIndex) {
				if (!dependsOn(baseIndex, parent[joinIndex], parent)) {
					return "A join type depends on two types which do not depend on each other.";
				}
				parent[joinIndex] = baseIndex;
			}
		}

		// check whether each type (except the first) has a join condition
		for (int i = 1; i < conditionFound.length; i++) {
			if (!conditionFound[i]) {
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
	public static class SpatialJoinCondition {

		/**
		 * The geometry property of the base type.
		 */
		public final PropertyEntityDefinition baseProperty;
		/**
		 * The geometry property of the type to join.
		 */
		public final PropertyEntityDefinition joinProperty;

		/**
		 * The spatial relation to be tested
		 */
		public final String relation;

		/**
		 * Constructs a join condition to join the type of
		 * <code>joinProperty</code> if the condition
		 * <code>joinProperty = baseProperty</code> matches.
		 * 
		 * @param baseProperty the property of a base type
		 * @param joinProperty the property of the type to join
		 * @param relation the spatial relation evaluator
		 */
		public SpatialJoinCondition(PropertyEntityDefinition baseProperty,
				PropertyEntityDefinition joinProperty, String relation) {
			this.baseProperty = baseProperty;
			this.joinProperty = joinProperty;
			this.relation = relation;
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
			result = prime * result + ((relation == null) ? 0 : relation.hashCode());
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			SpatialJoinCondition other = (SpatialJoinCondition) obj;
			if (baseProperty == null) {
				if (other.baseProperty != null) {
					return false;
				}
			}
			else if (!baseProperty.equals(other.baseProperty)) {
				return false;
			}

			if (joinProperty == null) {
				if (other.joinProperty != null) {
					return false;
				}
			}
			else if (!joinProperty.equals(other.joinProperty)) {
				return false;
			}

			return true;
		}
	}

}
