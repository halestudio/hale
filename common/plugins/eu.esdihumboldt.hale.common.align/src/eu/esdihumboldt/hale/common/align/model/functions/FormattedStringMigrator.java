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

package eu.esdihumboldt.hale.common.align.model.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.migrate.impl.DefaultCellMigrator;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.CellLog;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Migrator for FormattedString function.
 * 
 * @author Simon Templer
 */
public class FormattedStringMigrator extends DefaultCellMigrator {

	@Override
	public MutableCell updateCell(Cell originalCell, AlignmentMigration migration,
			MigrationOptions options, SimpleLog log) {
		MutableCell result = super.updateCell(originalCell, migration, options, log);
		log = SimpleLog.all(log, new CellLog(result, CELL_LOG_CATEGORY));

		if (options.updateSource()) {
			ListMultimap<String, ParameterValue> modParams = ArrayListMultimap
					.create(result.getTransformationParameters());
			List<ParameterValue> patternParams = modParams
					.get(FormattedStringFunction.PARAMETER_PATTERN);
			if (!patternParams.isEmpty()) {
				String pattern = patternParams.get(0).as(String.class);
				if (pattern != null) {
					patternParams.clear();
					patternParams.add(new ParameterValue(Value.of(
							convertPattern(pattern, originalCell.getSource(), migration, log))));
				}
			}
			result.setTransformationParameters(modParams);
		}

		return result;
	}

	private String convertPattern(String pattern, ListMultimap<String, ? extends Entity> oldSource,
			AlignmentMigration migration, SimpleLog log) {

		List<PropertyEntityDefinition> oldVars = oldSource
				.get(FormattedStringFunction.ENTITY_VARIABLE).stream()
				.filter(e -> e.getDefinition() instanceof PropertyEntityDefinition)
				.map(e -> (PropertyEntityDefinition) e.getDefinition())
				.collect(Collectors.toList());

		Map<String, Object> replacements = new HashMap<>();
		for (PropertyEntityDefinition var : oldVars) {
			Optional<EntityDefinition> replacement = migration.entityReplacement(var, log);
			replacement.ifPresent(repl -> {
				String newName = repl.getDefinition().getName().getLocalPart();
				// XXX there might be name conflicts - check for those or use
				// long names?

				FormattedStringFunction.addValue(replacements, newName, var);
			});

		}

		for (Entry<String, Object> replacement : replacements.entrySet()) {
			// replace variables
			pattern = pattern.replaceAll(Pattern.quote("{" + replacement.getKey() + "}"),
					"{" + replacement.getValue() + "}");
		}

		return pattern;
	}

}
