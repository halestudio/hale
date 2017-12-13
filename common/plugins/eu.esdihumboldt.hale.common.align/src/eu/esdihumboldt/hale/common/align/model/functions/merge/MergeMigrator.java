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
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Cell migrator for Merge.
 * 
 * @author Simon Templer
 */
public class MergeMigrator extends DefaultCellMigrator {

	private static final ALogger log = ALoggerFactory.getLogger(MergeMigrator.class);

	private static final String[] PROPERTY_PATH_PARAMETERS = { MergeFunction.PARAMETER_PROPERTY,
			MergeFunction.PARAMETER_ADDITIONAL_PROPERTY };

	@Override
	public MutableCell updateCell(Cell originalCell, AlignmentMigration migration,
			MigrationOptions options) {
		MutableCell result = super.updateCell(originalCell, migration, options);

		if (options.updateSource() && originalCell.getSource() != null) {
			Entity sourceType = CellUtil.getFirstEntity(originalCell.getSource());
			if (sourceType != null) {
				TypeDefinition sourceDef = sourceType.getDefinition().getType();

				ListMultimap<String, ParameterValue> modParams = ArrayListMultimap
						.create(result.getTransformationParameters());

				for (String property : PROPERTY_PATH_PARAMETERS) {
					updateProperties(modParams, migration, sourceDef, property);
				}

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

		Optional<EntityDefinition> replacement = migration.entityReplacement(entity);
		if (replacement.isPresent()) {
			return convertProperty(value, replacement.get());
		}
		else {
			// use original path
			return value;
		}
	}

	private ParameterValue convertProperty(ParameterValue value, EntityDefinition replacingEntity) {
		try {
			// yield replacement path
			List<QName> newPath = replacingEntity.getPropertyPath().stream()
					.map(context -> context.getChild().getName()).collect(Collectors.toList());
			return MergeUtil.toPropertyParameter(newPath);
		} catch (Exception e) {
			log.error("Unable to perform migration for merge property", e);
			return value;
		}
	}

}
