/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.json.ui.util;

/**
 * Enumeration for different types of winding orders for Geometry. Selected type
 * will be applied geometry. In case of Polygon/MultiPolygon selected type will
 * be applied to the shell and the reversed type is applied to the holes.
 * 
 * @author Arun
 */
public enum EnumJSONAutoOrderTypes {

	/**
	 * Auto detection of mode to use (limited to FeatureCollection and Json
	 * array).
	 */
	autodetect("Auto-detection (supports GeoJson FeatureCollection and Json array)"),

	/**
	 * JSON is a single object
	 */
	singleObject("Load as single object"),

	/**
	 * The first array found in the json is used a objec collection.
	 */
	firstArray("Use first Json array found as object collection");
	
	
	private String jsonModeOrder;

	private EnumJSONAutoOrderTypes(String jsonModeOrder) {
		this.jsonModeOrder = jsonModeOrder;
	}

	/**
	 * To get description of JSON mode order
	 * 
	 * @return JSON mode order
	 */
	public String getJsonModeOrder() {
		return jsonModeOrder;
	}

}
