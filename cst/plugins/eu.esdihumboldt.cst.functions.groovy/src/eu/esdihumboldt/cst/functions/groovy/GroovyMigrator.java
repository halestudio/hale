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

package eu.esdihumboldt.cst.functions.groovy;

import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.migrate.impl.DefaultCellMigrator;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Migrator for Groovy script cells that add warning about changed source or
 * target entities.
 * 
 * @author Simon Templer
 */
public class GroovyMigrator extends DefaultCellMigrator {

	@Override
	protected void postUpdateCell(MutableCell result, MigrationOptions options,
			boolean entitiesReplaced, SimpleLog log) {
		if (entitiesReplaced) {
			addWarnings(options, log);
		}

		super.postUpdateCell(result, options, entitiesReplaced, log);
	}

	/**
	 * Add warnings about Groovy script migration to the given log.
	 * 
	 * @param options the migration options
	 * @param log the log
	 */
	public static void addWarnings(MigrationOptions options, SimpleLog log) {
		String name;
		if (options.updateSource() && !options.updateTarget()) {
			name = "source entities";
		}
		else if (options.updateTarget() && !options.updateSource()) {
			name = "target entities";
		}
		else {
			name = "entities";
		}

		log.warn(
				"The cell''s {0} have been replaced, the new entities may have a different structure and/or names and may require updating the Groovy script accordingly",
				name);
	}

}
