/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.model.functions.join;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.migrate.impl.DefaultCellMigrator;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Cell migrator for joins.
 * 
 * @author Simon Templer
 */
public class JoinMigrator extends DefaultCellMigrator {

	@Override
	public MutableCell updateCell(Cell originalCell, AlignmentMigration migration,
			MigrationOptions options, SimpleLog log) {
		MutableCell result = super.updateCell(originalCell, migration, options, log);

		if (options.updateSource()) {
			ListMultimap<String, ParameterValue> modParams = ArrayListMultimap
					.create(result.getTransformationParameters());
			List<ParameterValue> joinParams = modParams.get(JoinFunction.PARAMETER_JOIN);
			if (!joinParams.isEmpty()) {
				JoinParameter joinParam = joinParams.get(0).as(JoinParameter.class);
				if (joinParam != null) {
					joinParams.clear();
					joinParams.add(new ParameterValue(Value
							.complex(convertJoinParameter(joinParam, migration, options, log))));
				}
			}
			result.setTransformationParameters(modParams);
		}

		return result;
	}

	/**
	 * Migrate a join parameter based on an {@link AlignmentMigration}.
	 * 
	 * @param joinParam the join parameter to migrate
	 * @param migration the alignment migration
	 * @param options the migration options
	 * @param log the migration log
	 * @return the migrated join parameter
	 */
	private JoinParameter convertJoinParameter(JoinParameter joinParam,
			AlignmentMigration migration, MigrationOptions options, SimpleLog log) {

		List<TypeEntityDefinition> types = joinParam.types.stream().map(type -> {
			return (TypeEntityDefinition) migration.entityReplacement(type, log).orElse(type);
		}).collect(Collectors.toList());

		Set<JoinCondition> conditions = joinParam.conditions.stream().map(condition -> {
			PropertyEntityDefinition baseProperty = (PropertyEntityDefinition) migration
					.entityReplacement(condition.baseProperty, log).orElse(condition.baseProperty);
			PropertyEntityDefinition joinProperty = (PropertyEntityDefinition) migration
					.entityReplacement(condition.joinProperty, log).orElse(condition.joinProperty);
			JoinCondition result = new JoinCondition(baseProperty, joinProperty);
			return result;
		}).collect(Collectors.toSet());

		return new JoinParameter(types, conditions);
	}

}
