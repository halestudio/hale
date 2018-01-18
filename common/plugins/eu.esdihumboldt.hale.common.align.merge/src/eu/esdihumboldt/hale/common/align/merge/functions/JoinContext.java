/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.merge.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.util.Pair;

/**
 * Context for merge of a Join cell.
 * 
 * @author Simon Templer
 */
public class JoinContext {

	private final JoinParameter orgParameter;

	/**
	 * Replacements for assembling order
	 */
	private final ListMultimap<TypeEntityDefinition, TypeEntityDefinition> replacements;

	/**
	 * Match cells that are Joins
	 */
	private final List<Cell> joinMatches = new ArrayList<>();

	/**
	 * Create new context for merging the given cell.
	 * 
	 * @param originalCell the cell to migrate
	 */
	public JoinContext(Cell originalCell) {
		this.orgParameter = CellUtil.getFirstParameter(originalCell, JoinFunction.PARAMETER_JOIN)
				.as(JoinParameter.class);
		this.replacements = ArrayListMultimap.create();
	}

	/**
	 * Apply information collected in the context to the cell.
	 * 
	 * @param newCell the merged cell
	 * @param log the cell log
	 * @param migration the alignment migration
	 */
	public void apply(MutableCell newCell, AlignmentMigration migration, SimpleLog log) {
		ListMultimap<String, ParameterValue> params = ArrayListMultimap.create();

		/*
		 * Order: Keep original order but replace entities w/ all matches
		 */
		Set<TypeEntityDefinition> types = new LinkedHashSet<>();
		for (TypeEntityDefinition type : orgParameter.types) {
			List<TypeEntityDefinition> repl = replacements.get(type);
			if (repl.isEmpty()) {
				log.error("Could not find replacement for type {0} in join order");
				types.add(type);
			}
			else {
				types.addAll(repl);
			}
		}

		/*
		 * Conditions: (1) add conditions from matches and (2) add conditions
		 * from original cell translated to new schema (via property mapping),
		 * if they are not duplicates
		 */
		Set<Pair<PropertyEntityDefinition, PropertyEntityDefinition>> cons = new LinkedHashSet<>();

		// add conditions from matches
		for (Cell match : joinMatches) {
			JoinParameter matchParameter = CellUtil
					.getFirstParameter(match, JoinFunction.PARAMETER_JOIN).as(JoinParameter.class);
			for (JoinCondition condition : matchParameter.conditions) {
				cons.add(new Pair<>(condition.baseProperty, condition.joinProperty));
			}
		}

		// migrate original conditions
		Set<JoinCondition> migrated = orgParameter.conditions.stream().map(condition -> {
			PropertyEntityDefinition baseProperty = (PropertyEntityDefinition) migration
					.entityReplacement(condition.baseProperty, log).orElse(condition.baseProperty);
			PropertyEntityDefinition joinProperty = (PropertyEntityDefinition) migration
					.entityReplacement(condition.joinProperty, log).orElse(condition.joinProperty);
			JoinCondition result = new JoinCondition(baseProperty, joinProperty);
			return result;
		}).collect(Collectors.toSet());
		for (JoinCondition condition : migrated) {
			if (!condition.baseProperty.equals(condition.joinProperty)) {
				// migrated condition may contain "loop" condition

				cons.add(new Pair<>(condition.baseProperty, condition.joinProperty));
			}
		}

		// all conditions
		Set<JoinCondition> conditions = new HashSet<>();
		for (Pair<PropertyEntityDefinition, PropertyEntityDefinition> condition : cons) {
			conditions.add(new JoinCondition(condition.getFirst(), condition.getSecond()));
		}

		JoinParameter newParam = new JoinParameter(new ArrayList<>(types), conditions);
		params.replaceValues(JoinFunction.PARAMETER_JOIN,
				Collections.singleton(new ParameterValue(Value.of(newParam))));
		newCell.setTransformationParameters(params);
	}

	/**
	 * Specify types that should replace a given type in the join order.
	 * 
	 * @param source the type to replace
	 * @param matches the types to replace it with
	 */
	public void addOrderReplacement(TypeEntityDefinition source, TypeEntityDefinition... matches) {
		replacements.putAll(source, Arrays.asList(matches));
	}

	/**
	 * Add a matched Join cell (for collecting match conditions).
	 * 
	 * @param match the match
	 */
	public void addJoinMatch(Cell match) {
		joinMatches.add(match);
	}

}
