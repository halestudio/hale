/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json.writer;

/**
 * Enum used to generate JSON from instances.
 * 
 * @author Simon Templer
 */
public enum Placement {
	/**
	 * The element is the root of the Json document.
	 */
	ROOT,
	/**
	 * The element is a member in a GeoJson FeatureCollection.
	 */
	MEMBER,
	/**
	 * The element is a value within a property.
	 */
	VALUE;
}
