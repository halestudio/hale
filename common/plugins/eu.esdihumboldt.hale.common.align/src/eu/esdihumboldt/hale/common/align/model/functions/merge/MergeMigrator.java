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

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigrationNameLookupSupport;
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

	private static final String[] PROPERTY_PATH_PARAMETERS = { MergeFunction.PARAMETER_PROPERTY,
			MergeFunction.PARAMETER_ADDITIONAL_PROPERTY };

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

				for (String property : PROPERTY_PATH_PARAMETERS) {
					updateProperties(modParams, migration, sourceDef, property, log);
				}

				result.setTransformationParameters(modParams);
			}
		}

		return result;
	}

	private void updateProperties(ListMultimap<String, ParameterValue> modParams,
			AlignmentMigration migration, TypeDefinition sourceType, String parameterProperty,
			SimpleLog log) {
		List<ParameterValue> params = modParams.get(parameterProperty);

		List<ParameterValue> newParams = params.stream()
				.map(property -> convertProperty(property, migration, sourceType, log))
				.collect(Collectors.toList());
		params.clear();
		params.addAll(newParams);
	}

	private ParameterValue convertProperty(ParameterValue value, AlignmentMigration migration,
			TypeDefinition sourceType, SimpleLog log) {

		EntityDefinition entity = null;
		try {
			entity = MergeUtil.resolvePropertyPath(value, sourceType);
		} catch (IllegalStateException e) {
			// If the migration supports lookup via entity name, try to find a
			// replacement for the parameter value
			if (migration instanceof AlignmentMigrationNameLookupSupport) {
				AlignmentMigrationNameLookupSupport nameLookup = (AlignmentMigrationNameLookupSupport) migration;
				entity = nameLookup.entityReplacement(value.getStringRepresentation()).orElse(null);
			}
		}

		if (entity == null) {
			return value;
		}

		Optional<EntityDefinition> replacement = migration.entityReplacement(entity, log);
		if (replacement.isPresent()) {
			return convertProperty(value, replacement.get(), log);
		}
		else {
			// use original path
			return value;
		}
	}

	private ParameterValue convertProperty(ParameterValue value, EntityDefinition replacingEntity,
			SimpleLog log) {
		try {
			// yield replacement path
			List<QName> newPath = replacingEntity.getPropertyPath().stream()
					.map(context -> context.getChild().getName()).collect(Collectors.toList());
			return MergeUtil.toPropertyParameter(newPath);
		} catch (Exception e) {
			log.error(MessageFormat.format("Merge property configuration {0} could not be updated",
					value.as(String.class)), e);
			return value;
		}
	}

}
