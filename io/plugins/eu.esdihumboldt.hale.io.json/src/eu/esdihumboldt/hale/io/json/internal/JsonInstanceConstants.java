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

package eu.esdihumboldt.hale.io.json.internal;

import eu.esdihumboldt.hale.io.json.JsonInstanceReader;

/**
 * Constants used to generate JSON from instances.
 * 
 * @author Simon Templer
 */
public interface JsonInstanceConstants {

	/**
	 * see http://wiki.geojson.org/GeoJSON_draft_version_6
	 */
	public static final String NAMESPACE_GEOJSON = "http://geojson.org/ns#";

	/**
	 * see http://wiki.geojson.org/GeoJSON_draft_version_6
	 */
	public static final String NAMESPACE_INSTANCE_JSON = "http://wetransform.to/hale/json/instance";

	/**
	 * see http://wiki.geojson.org/GeoJSON_draft_version_6
	 */
	public static final String PREFIX_INSTANCE_JSON = "hj";

	/**
	 * Name of the parameter specifying the type name
	 */
	public static String PARAM_TYPENAME = "typename";

	/**
	 * Name of the parameter for {@link JsonInstanceReader} to auto detect
	 * schema types when selecting instances for multiple schemas.
	 */
	public static final String PARAM_AUTO_DETECT_SCHEMA_TYPES = "autoDetectSchemaTypes";

	/**
	 * Name of the parameter for {@link JsonInstanceReader} to activate matching
	 * of json property names to schema property names by checking if there is
	 * exactly one schema property whose name starts with the json property
	 * name.
	 */
	public static final String PARAM_MATCH_SHORT_PROPERTY_NAMES = "matchShortPropertyNames";

}