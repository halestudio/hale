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

package eu.esdihumboldt.hale.common.align.merge.impl;

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * Default cell merger.
 * 
 * @author Simon Templer
 */
public class DefaultMergeCellMigrator extends AbstractMergeCellMigrator<Void> {

	@Override
	protected Void newContext(Cell originalCell) {
		return null;
	}

}
