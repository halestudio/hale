/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.postgresql;

import org.postgresql.PGConnection;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.GeometryAdvisor;

/**
 * Geometry advisor for PostGIS.
 * 
 * @author Simon Templer
 */
public class PostGISGeometries implements GeometryAdvisor<PGConnection> {

	@Override
	public boolean isFixedType(ColumnDataType columnType) {
		/*
		 * Concrete information on geometry type and SRS is not stored in the
		 * column but as metadata in the database. Therefore every column has to
		 * be configured on its own.
		 */
		return false;
	}

	@Override
	public Class<? extends Geometry> configureGeometryColumnType(PGConnection connection,
			Column column, DefaultTypeDefinition type) {
		// TODO determine SRS and store that information using a constraint
		// TODO determine actual geometry type
		return Geometry.class;
	}

	@Override
	public Object convertGeometry(GeometryProperty<?> geom, TypeDefinition columnType) {
		// TODO convert geometry to target SRS
		// TODO convert geometry to PGgeometry
		return geom.getGeometry();
	}

}
