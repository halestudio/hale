/*
 * Copyright (c) 2021 wetransform GmbH
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
 * 
 * Modes for determining which spatial index should be used when writing a new
 * GeoPackage file.
 * 
 * TODO Replace by extension-based approach where index types are provided via
 * an extension point, similar to the way InterpolationAlgorithm is handled
 * 
 * @author Florian Esser
 */
public enum GeopackageSpatialIndexType {

	/**
	 * RTree spatial index, see
	 * http://www.geopackage.org/spec121/#extension_rtree
	 */
	RTREE("RTree Spatial Index", "rtree"),

	/**
	 * NGA Geometry Index, see
	 * http://ngageoint.github.io/GeoPackage/docs/extensions/geometry-index.
	 * html
	 */
	NGA("NGA Geometry Index", "nga"),

	/**
	 * No spatial index
	 */
	NONE("Disable index creation", "none");

	private final String description;
	private final String parameterValue;

	GeopackageSpatialIndexType(String description, String parameterValue) {
		this.description = description;
		this.parameterValue = parameterValue;
	}

	@SuppressWarnings("javadoc")
	public String getDescription() {
		return description;
	}

	@SuppressWarnings("javadoc")
	public String getParameterValue() {
		return parameterValue;
	}
}