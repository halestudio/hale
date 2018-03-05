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
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinMigrator;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Groovy join migrator.
 * 
 * @author Simon Templer
 */
public class GroovyJoinMigrator extends JoinMigrator {

	@Override
	protected void postUpdateCell(MutableCell result, MigrationOptions options,
			boolean entitiesReplaced, SimpleLog log) {
		if (entitiesReplaced) {
			GroovyMigrator.addWarnings(options, log);
		}

		super.postUpdateCell(result, options, entitiesReplaced, log);
	}

}
