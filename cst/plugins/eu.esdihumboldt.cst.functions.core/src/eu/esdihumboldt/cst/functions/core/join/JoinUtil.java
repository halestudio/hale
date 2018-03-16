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

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Utility methods for Join operations
 * 
 * @author Florian Esser
 */
public class JoinUtil {

	/**
	 * Helper class to bundle together the join table and the joined properties
	 * 
	 * @author Florian Esser
	 */
	public static class JoinDefinition {

		/**
		 * ChildType -> (ParentType -> Collection<JoinCondition>)
		 */
		public final Map<Integer, Multimap<Integer, JoinCondition>> joinTable = new HashMap<>();

		/**
		 * All joined properties
		 */
		public final Multimap<TypeDefinition, PropertyEntityDefinition> properties = HashMultimap
				.create();

		/**
		 * All base properties
		 */
		public final Multimap<TypeDefinition, PropertyEntityDefinition> baseProperties = HashMultimap
				.create();

		/**
		 * ChildType -> DirectParentType
		 */
		public final int[] directParent;

		private JoinDefinition(int typeCount) {
			directParent = new int[typeCount];
		}
	}

	/**
	 * Build the join table and joined properties maps based on the given join
	 * parameter
	 * 
	 * @param joinParameter The join parameter
	 * @return A {@link JoinDefinition} containing the join table and joined
	 *         properties maps
	 */
	public static JoinDefinition getJoinDefinition(JoinParameter joinParameter) {
		JoinDefinition result = new JoinDefinition(joinParameter.getTypes().size());

		for (JoinCondition condition : joinParameter.getConditions()) {
			int baseTypeIndex = joinParameter.getTypes()
					.indexOf(AlignmentUtil.getTypeEntity(condition.baseProperty));
			int joinTypeIndex = joinParameter.getTypes()
					.indexOf(AlignmentUtil.getTypeEntity(condition.joinProperty));
			Multimap<Integer, JoinCondition> typeTable = result.joinTable.get(joinTypeIndex);
			if (typeTable == null) {
				typeTable = ArrayListMultimap.create(2, 2);
				result.joinTable.put(joinTypeIndex, typeTable);
			}
			typeTable.put(baseTypeIndex, condition);

			// update highest type if necessary
			if (result.directParent[joinTypeIndex] < baseTypeIndex) {
				result.directParent[joinTypeIndex] = baseTypeIndex;
			}

			result.properties.put(condition.joinProperty.getType(), condition.joinProperty);
			result.baseProperties.put(condition.baseProperty.getType(), condition.baseProperty);
		}

		return result;
	}

}
