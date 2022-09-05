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

package eu.esdihumboldt.hale.io.json;

/**
 * Writes instances as TopoJSON.
 * 
 * @author Flaminia Catalli
 */
public class TopoJSONInstanceWriter extends JsonInstanceWriter {

//	/**
//	 * Parameter name for the default geometry association.
//	 * 
//	 * @deprecated as of release 4.2.0 because we don't use geometry
//	 *             configuration for geoJson and export the data in WG84 format.
//	 *             As geoJson expects WGS 84 with lon/lat (see
//	 *             https://tools.ietf.org/html/rfc7946)
//	 */
//	@Deprecated
//	public static final String PARAM_GEOMETRY_CONFIG = "geojson.geometry.config";

	/**
	 * @param geoJson use geoJson feature when exporting to geoJson
	 * @param topoJson use topoJson feature when exporting to topoJson
	 */

	public TopoJSONInstanceWriter() {
		super(false, true);
	}

	@Override
	protected String getDefaultTypeName() {
		return "TopoJSON";

	}

}
