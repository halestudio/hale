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

package eu.esdihumboldt.hale.common.align.migrate;

/**
 * Migration options interface.
 * 
 * @author Simon Templer
 */
public interface MigrationOptions {

	/**
	 * @return if alignment sources should be updated
	 */
	boolean updateSource();

	/**
	 * @return if alignment targets should be updated
	 */
	boolean updateTarget();

	/**
	 * @return if base aligment content should be transfered to the updated
	 *         alignment
	 */
	boolean transferBase();

}
