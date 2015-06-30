/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.spatialite.internal;

/**
 * Class holding Spatial Reference System metadata.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SrsMetadata {

	private final String srText;
	private final int authSrid;
	private final String authName;
	private final int srid;

	/**
	 * 
	 * @param srText The SR WKT definition
	 * @param authSrid The SR ID
	 * @param authName The authority name e.g. EPSG
	 * @param srid the global SR ID
	 */
	public SrsMetadata(String srText, int authSrid, String authName, int srid) {
		super();
		this.srText = srText;
		this.authSrid = authSrid;
		this.authName = authName;
		this.srid = srid;
	}

	/**
	 * @return the srText
	 */
	public String getSrText() {
		return srText;
	}

	/**
	 * @return the authSrid
	 */
	public int getAuthSrid() {
		return authSrid;
	}

	/**
	 * @return the authName
	 */
	public String getAuthName() {
		return authName;
	}

	/**
	 * @return the srid
	 */
	public int getSrid() {
		return srid;
	}

}
