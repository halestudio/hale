/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.presets.extension;

/**
 * Common interface for comparating {@link SchemaCategory} and
 * {@link SchemaPreset}.
 * 
 * @author Simon Templer
 */
public interface Named {

	/**
	 * Get the object's name.
	 * 
	 * @return the object name
	 */
	public abstract String getName();

}
