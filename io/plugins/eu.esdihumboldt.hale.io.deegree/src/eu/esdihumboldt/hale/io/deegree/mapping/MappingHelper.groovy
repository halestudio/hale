/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.deegree.mapping

import static org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension.DIM_2

import org.deegree.cs.coordinatesystems.ICRS
import org.deegree.feature.persistence.sql.GeometryStorageParams
import org.deegree.feature.persistence.sql.MappedAppSchema
import org.deegree.feature.persistence.sql.mapper.AppSchemaMapper
import org.deegree.feature.types.AppSchema
import org.deegree.gml.schema.GMLAppSchemaReader
import org.deegree.sqldialect.SQLDialect

import groovy.transform.CompileStatic

/**
 * Helper class to generate DDL and deegree FeatureStore configuration files based on GML schema.
 */
@CompileStatic
class MappingHelper {

	public static MappedAppSchema mapApplicationSchema(SQLDialect dialect,
			AppSchema appSchema, ICRS crs, boolean blobMapping) {
		String databaseSrid = dialect.getUndefinedSrid()
		GeometryStorageParams storageParams = new GeometryStorageParams( crs, databaseSrid, DIM_2 )

		AppSchemaMapper mapper = new AppSchemaMapper(appSchema, blobMapping,
				!blobMapping, storageParams,
				// Maximum table name length - is the dialect value correct?
				dialect.getMaxTableNameLength(),
				// Namespace prefix prefix for table names
				true,
				// no integer IDs!
				false)
		return mapper.getMappedSchema()
	}

	public static AppSchema readApplicationSchema(String schemaLoc) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		GMLAppSchemaReader decoder = new GMLAppSchemaReader(null, null, schemaLoc)
		return decoder.extractAppSchema()
	}
}
