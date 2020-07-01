/*
 * Copyright (c) 2020 wetransform GmbH
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

package eu.esdihumboldt.hale.io.geopackage;

/**
 * Type of supported GeoPackage table.
 * 
 * @author Simon Templer
 */
public enum GeopackageTableType {

	/**
	 * A feature table including a geometry.
	 */
	FEATURE,
	/**
	 * A table without geometry.
	 */
	ATTRIBUTE

}
