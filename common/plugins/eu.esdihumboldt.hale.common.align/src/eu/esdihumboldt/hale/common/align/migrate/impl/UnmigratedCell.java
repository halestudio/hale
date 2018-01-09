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

package eu.esdihumboldt.hale.common.align.migrate.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigrationNameLookupSupport;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.MutableCellDecorator;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Decorator for a {@link MutableCell} that allows to do lazy migration.
 * 
 * @author Florian Esser
 */
public class UnmigratedCell extends MutableCellDecorator {

	private final CellMigrator migrator;
	private final Map<EntityDefinition, EntityDefinition> entityMappings;

	/**
	 * Create an unmigrated cell
	 * 
	 * @param unmigratedCell Original cell that is to be migrated later
	 * @param migrator The migrator to apply
	 * @param mappings The original {@link EntityDefinition}s mapped to the
	 *            resolved ones
	 */
	public UnmigratedCell(MutableCell unmigratedCell, CellMigrator migrator,
			Map<EntityDefinition, EntityDefinition> mappings) {
		super(unmigratedCell);
		this.migrator = migrator;
		this.entityMappings = mappings;
	}

	/**
	 * @return true if a migrator was defined
	 */
	public boolean canMigrate() {
		return migrator != null;
	}

	/**
	 * Perform the migration of the original cell and return the migrated cell.
	 * The <code>UnmigratedCell</code> instance is not changed.
	 * 
	 * @param additionalMappings Additional mappings of original
	 *            {@link EntityDefinition}s to the resolved ones that should be
	 *            considered in the migration
	 * @param log the log
	 * @return the migrated cell
	 */
	public MutableCell migrate(Map<EntityDefinition, EntityDefinition> additionalMappings,
			SimpleLog log) {
		final Map<EntityDefinition, EntityDefinition> joinedMappings = new HashMap<>(
				entityMappings);
		joinedMappings.putAll(additionalMappings);

		AlignmentMigration migration = new AlignmentMigrationNameLookupSupport() {

			@Override
			public Optional<EntityDefinition> entityReplacement(EntityDefinition entity,
					SimpleLog log) {
				return Optional.ofNullable(joinedMappings.get(entity));
			}

			@Override
			public Optional<EntityDefinition> entityReplacement(String name) {
				for (EntityDefinition original : joinedMappings.keySet()) {
					QName entityName = original.getDefinition().getName();
					if (entityName != null && entityName.getLocalPart().equals(name)) {
						return Optional.of(original);
					}
				}

				return Optional.empty();
			}
		};

		MigrationOptions options = new MigrationOptions() {

			@Override
			public boolean updateTarget() {
				return true;
			}

			@Override
			public boolean updateSource() {
				return true;
			}

			@Override
			public boolean transferBase() {
				return false;
			}
		};

		return migrator.updateCell(this, migration, options, log);
	}

	/**
	 * @return the mappings of original entity definition to the resolved ones
	 */
	public Map<EntityDefinition, EntityDefinition> getEntityMappings() {
		return entityMappings;
	}
}
