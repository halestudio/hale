/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model;

/**
 * Different transformation modes for type cells.
 * 
 * @author Simon Templer
 */
public enum TransformationMode {
	/**
	 * The cell is active and will initiate transformations.
	 */
	active("Active"),
	/**
	 * The cell will not initiate any transformations, but may be used in the
	 * context of others.
	 */
	passive("Passive"),
	/**
	 * The cell is completely disabled.
	 */
	disabled("Disabled");

	private final String displayName;

	private TransformationMode(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the mode's display name
	 */
	public String displayName() {
		return displayName;
	}
}
