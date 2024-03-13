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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.GroovyJoin;
import eu.esdihumboldt.hale.common.align.merge.MergeUtil;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.EntityMatch;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
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
	 * Original sources that were replaced and filters/contexts could not be
	 * retained.
	 */
	private final List<EntityDefinition> strippedSources = new ArrayList<>();

	/**
	 * Collected Groovy scripts
	 */
	private final List<Pair<Cell, String>> scripts = new ArrayList<>();

	/**
	 * Collected map of filters applied to join source types (only works because
	 * each type can only be used once in a Join)
	 */
	private final Map<TypeDefinition, Filter> typeFilters = new HashMap<>();

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
		for (TypeEntityDefinition type : orgParameter.getTypes()) {
			List<TypeEntityDefinition> repl = replacements.get(type);
			if (repl.isEmpty()) {
				log.error("Could not find replacement for type {0} in join order", type);
				types.add(type);
			}
			else {
				for (TypeEntityDefinition replacement : repl) {
					Filter filter = typeFilters.get(replacement.getDefinition());
					if (filter != null) {
						// apply filter
						types.add(new TypeEntityDefinition(replacement.getDefinition(),
								replacement.getSchemaSpace(), filter));
					}
					else {
						types.add(replacement);
					}
				}
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
			for (JoinCondition condition : matchParameter.getConditions()) {
				cons.add(new Pair<>(condition.baseProperty, condition.joinProperty));
			}
		}

		// migrate original conditions
		Set<JoinCondition> migrated = orgParameter.getConditions().stream().map(condition -> {
			PropertyEntityDefinition baseProperty = processOriginalConditionProperty(
					condition.baseProperty, migration, log);
			PropertyEntityDefinition joinProperty = processOriginalConditionProperty(
					condition.joinProperty, migration, log);
			JoinCondition result = new JoinCondition(baseProperty, joinProperty);
			return result;
		}).collect(Collectors.toSet());
		for (JoinCondition condition : migrated) {
			if (!condition.baseProperty.getType().equals(condition.joinProperty.getType())) {
				// migrated condition may contain "loop" condition

				cons.add(new Pair<>(condition.baseProperty, condition.joinProperty));
			}
		}

		// add messages on dropped filter/conditions
		for (EntityDefinition stripped : strippedSources) {
			if (!AlignmentUtil.isDefaultEntity(stripped)) {
				String msg = "Conditions/contexts for an original source could not be transfered and were dropped: "
						+ MergeUtil.getContextInfoString(stripped);
				log.warn(msg);
			}
		}

		// all conditions
		Set<JoinCondition> conditions = new HashSet<>();
		for (Pair<PropertyEntityDefinition, PropertyEntityDefinition> condition : cons) {
			conditions.add(new JoinCondition(applyFilter(condition.getFirst()),
					applyFilter(condition.getSecond())));
		}

		JoinParameter newParam = new JoinParameter(new ArrayList<>(types), conditions);
		params.replaceValues(JoinFunction.PARAMETER_JOIN,
				Collections.singleton(new ParameterValue(Value.of(newParam))));

		// script (Groovy Join)

		// Use Groovy Join if original or match uses a script
		if (!scripts.isEmpty()) {
			boolean originalScript = scripts.size() == 1
					&& GroovyJoin.ID.equals(newCell.getTransformationIdentifier());
			Text script;
			if (originalScript) {
				// use original script
				script = new Text(scripts.get(0).getSecond());

				// create annotation
				log.warn(
						"The Groovy script from the original cell was reused, logic and references to sources are very likely not valid anymore.");
			}
			else {
				// dummy script with all original scripts
				newCell.setTransformationIdentifier(GroovyJoin.ID);

				script = buildScript(scripts);

				// create annotation
				log.warn(
						"At least one source mapping used a Groovy script, the script could not be combined automatically and was replaced with a dummy script (old scripts are commented out). Please check how you can migrate the old functionality.");
			}

			params.replaceValues(GroovyConstants.PARAMETER_SCRIPT,
					Collections.singleton(new ParameterValue(Value.of(script))));
		}

		newCell.setTransformationParameters(params);
	}

	private PropertyEntityDefinition applyFilter(PropertyEntityDefinition property) {
		Filter filter = typeFilters.get(property.getType());
		if (filter != null) {
			return new PropertyEntityDefinition(property.getType(), property.getPropertyPath(),
					property.getSchemaSpace(), filter);
		}
		else {
			return property;
		}
	}

	/**
	 * Process a property that is part of the original join conditions.
	 * 
	 * @param property the property to process
	 * @param migration the alignment migration
	 * @param log the log
	 * @return the processed property
	 */
	private PropertyEntityDefinition processOriginalConditionProperty(
			PropertyEntityDefinition property, AlignmentMigration migration, SimpleLog log) {
		boolean isStripped = strippedSources.stream().anyMatch(e -> {
			return e.getType().equals(property.getType());
		});
		if (!isStripped) {
			return (PropertyEntityDefinition) migration.entityReplacement(property, log)
					.map(EntityMatch::getMatch).orElse(property);
		}
		else {
			EntityDefinition stripped = AlignmentUtil.getAllDefaultEntity(property);
			return (PropertyEntityDefinition) migration.entityReplacement(stripped, log)
					.map(EntityMatch::getMatch).orElse(stripped);
		}
	}

	private static Text buildScript(List<Pair<Cell, String>> scripts) {
		StringBuilder script = new StringBuilder();

		// add default script

		script.append("// FIXME dummy script creating a target instance\n");
		script.append("_target {\n");
		script.append("}\n");

		script.append("\n// Find the scripts from before the merge below:\n");

		for (Pair<Cell, String> p : scripts) {
			script.append("\n\n\n");

			String name = CellUtil.getCellDescription(p.getFirst(), null);
			script.append("// Cell: ");
			script.append(name);
			script.append("\n");

			for (String line : p.getSecond().split("\\r?\\n")) {
				script.append("\n// ");
				script.append(line);
			}
		}

		return new Text(script.toString());
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

	/**
	 * Add a Groovy script from the original cell or a match.
	 * 
	 * @param cell the associated cell
	 * @param script the Groovy script
	 */
	public void addGroovyScript(Cell cell, String script) {
		scripts.add(new Pair<>(cell, script));
	}

	/**
	 * Add a filter associated to a type used in the join.
	 * 
	 * @param type the type
	 * @param filter the filter associated to the type
	 */
	public void addTypeFilter(TypeDefinition type, Filter filter) {
		typeFilters.put(type, filter);
	}

	/**
	 * Add an original source that was replaced where filter/contexts were not
	 * retained.
	 * 
	 * @param source the source to add
	 */
	public void addStrippedSource(EntityDefinition source) {
		strippedSources.add(source);
	}

}
