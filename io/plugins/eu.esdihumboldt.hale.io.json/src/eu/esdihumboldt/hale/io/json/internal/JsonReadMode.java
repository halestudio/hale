/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json.internal;

/**
 * Different read modes for Json files.
 * 
 * @author Simon Templer
 */
public enum JsonReadMode {

	/**
	 * Auto detection of mode to use (limited to FeatureCollection and Json
	 * array).
	 */
	auto("Auto-detection (supports GeoJson FeatureCollection and Json array)"),
	/**
	 * Json is a single object.
	 */
	singleObject("Load as single object"),
	/**
	 * The first array found in the json is used a objec collection.
	 */
	firstArray("Use first Json array found as object collection");

	/**
	 * Read mode label.
	 */
	public final String label;

	private JsonReadMode(String label) {
		this.label = label;
	}

}
