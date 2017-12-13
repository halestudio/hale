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

package eu.esdihumboldt.hale.common.align.model.functions.merge;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.migrate.impl.DefaultCellMigrator;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Cell migrator for Merge.
 * 
 * @author Simon Templer
 */
public class MergeMigrator extends DefaultCellMigrator {

	private static final ALogger log = ALoggerFactory.getLogger(MergeMigrator.class);

	@Override
	public MutableCell updateCell(Cell originalCell, AlignmentMigration migration,
			MigrationOptions options, SimpleLog log) {
		MutableCell result = super.updateCell(originalCell, migration, options, log);

		if (options.updateSource() && originalCell.getSource() != null) {
			Entity sourceType = CellUtil.getFirstEntity(originalCell.getSource());
			if (sourceType != null) {
				TypeDefinition sourceDef = sourceType.getDefinition().getType();

				ListMultimap<String, ParameterValue> modParams = ArrayListMultimap
						.create(result.getTransformationParameters());

				updateProperties(modParams, migration, sourceDef, MergeFunction.PARAMETER_PROPERTY);
				updateProperties(modParams, migration, sourceDef,
						MergeFunction.PARAMETER_ADDITIONAL_PROPERTY);

				result.setTransformationParameters(modParams);
			}
		}

		return result;
	}

	private void updateProperties(ListMultimap<String, ParameterValue> modParams,
			AlignmentMigration migration, TypeDefinition sourceType, String parameterProperty) {
		List<ParameterValue> params = modParams.get(parameterProperty);

		List<ParameterValue> newParams = params.stream()
				.map(property -> convertProperty(property, migration, sourceType))
				.collect(Collectors.toList());
		params.clear();
		params.addAll(newParams);
	}

	private ParameterValue convertProperty(ParameterValue value, AlignmentMigration migration,
			TypeDefinition sourceType) {
		try {
			EntityDefinition entity = MergeUtil.resolvePropertyPath(value, sourceType);

			Optional<EntityDefinition> replacement = migration.entityReplacement(entity);
			if (replacement.isPresent()) {
				// yield replacement path
				List<QName> newPath = replacement.get().getPropertyPath().stream()
						.map(context -> context.getChild().getName()).collect(Collectors.toList());
				return MergeUtil.toPropertyParameter(newPath);
			}
			else {
				// use original path
				return value;
			}
		} catch (Exception e) {
			log.error("Unable to perform migration for merge property", e);
			return value;
		}
	}

}
