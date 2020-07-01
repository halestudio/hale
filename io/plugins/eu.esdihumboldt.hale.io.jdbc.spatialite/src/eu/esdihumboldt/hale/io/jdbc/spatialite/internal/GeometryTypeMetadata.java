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

import org.locationtech.jts.geom.Geometry;

/**
 * Class holding geometry type metadata (i.e. coordinate dimension, Spatial
 * Reference ID and JTS {@link Geometry} subclass that will be bound to this
 * geometry) for a single SpatiaLite geometry column.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class GeometryTypeMetadata {

	private final int srid;

	private final int coordDimension;

	private final Class<? extends Geometry> geomType;

	/**
	 * Constructor.
	 * 
	 * @param srid Spatial Reference ID
	 * @param geomType JST {@link Geometry} subclass
	 * @param coordDimension coordinate dimension
	 */
	public GeometryTypeMetadata(int srid, Class<? extends Geometry> geomType, int coordDimension) {
		super();
		this.srid = srid;
		this.geomType = geomType;
		this.coordDimension = coordDimension;
	}

	/**
	 * @return the srid
	 */
	public int getSrid() {
		return srid;
	}

	/**
	 * @return the geomType
	 */
	public Class<? extends Geometry> getGeomType() {
		return geomType;
	}

	/**
	 * @return the coordDimension
	 */
	public int getCoordDimension() {
		return coordDimension;
	}

}
