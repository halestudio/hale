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

import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;

/**
 * Migration options default implementation.
 * 
 * @author Simon Templer
 */
public class MigrationOptionsImpl implements MigrationOptions {

	private final boolean updateSource;
	private final boolean updateTarget;
	private final boolean transferBase;

	/**
	 * Create a migration options object specifying all settings.
	 * 
	 * @param updateSource if the migration should update source entities
	 * @param updateTarget if the migration should update target entities
	 * @param transferBase if the migration should transfer base alignment
	 *            content to the updated alignment
	 */
	public MigrationOptionsImpl(boolean updateSource, boolean updateTarget, boolean transferBase) {
		super();
		this.updateSource = updateSource;
		this.updateTarget = updateTarget;
		this.transferBase = transferBase;
	}

	@Override
	public boolean updateSource() {
		return updateSource;
	}

	@Override
	public boolean updateTarget() {
		return updateTarget;
	}

	@Override
	public boolean transferBase() {
		return transferBase;
	}

}
