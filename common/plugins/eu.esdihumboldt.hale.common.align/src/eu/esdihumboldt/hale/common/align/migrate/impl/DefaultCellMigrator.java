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

package eu.esdihumboldt.hale.common.align.migrate.impl;

import java.util.Optional;

import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Default implementation of migrator for single cells.
 * 
 * @author Simon Templer
 */
public class DefaultCellMigrator implements CellMigrator {

	@Override
	public MutableCell updateCell(final Cell originalCell, final AlignmentMigration migration,
			final MigrationOptions options, SimpleLog log) {
		MutableCell result = new DefaultCell(originalCell);

		EntryTransformer<String, Entity, Entity> entityTransformer = new EntryTransformer<String, Entity, Entity>() {

			@Override
			public Entity transformEntry(String key, Entity value) {
				EntityDefinition org = value.getDefinition();

				Optional<EntityDefinition> replace = migration.entityReplacement(org, log);

				EntityDefinition entity = replace.orElse(org);
				// FIXME what about null replacements / removals?

				if (entity instanceof PropertyEntityDefinition) {
					return new DefaultProperty((PropertyEntityDefinition) entity);
				}
				else if (entity instanceof TypeEntityDefinition) {
					return new DefaultType((TypeEntityDefinition) entity);
				}
				else {
					throw new IllegalStateException(
							"Invalid entity definition for creating entity");
				}
			}
		};

		// update source entities
		if (options.updateSource() && result.getSource() != null && !result.getSource().isEmpty()) {
			result.setSource(Multimaps.transformEntries(result.getSource(), entityTransformer));
		}

		// update target entities
		if (options.updateTarget() && result.getTarget() != null && !result.getTarget().isEmpty()) {
			result.setTarget(Multimaps.transformEntries(result.getTarget(), entityTransformer));
		}

		// TODO anything else?

		return result;
	}

}
