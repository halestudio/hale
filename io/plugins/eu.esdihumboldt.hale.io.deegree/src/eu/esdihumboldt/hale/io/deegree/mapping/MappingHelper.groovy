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

	public static MappedAppSchema mapApplicationSchema(AppSchema appSchema, MappingConfiguration config) {
		GeometryStorageParams storageParams = config.getGeometryStorageParameters();

		boolean blobMapping = config.getMode().equals(MappingMode.blob);

		AppSchemaMapper mapper = new AppSchemaMapper(appSchema, blobMapping,
				!blobMapping, storageParams,
				// Maximum table name length
				config.getMaxNameLength().orElseGet {
					// fall back to dialect
					SQLDialect dialect = config.getSQLDialect()
					return Math.min(dialect.maxTableNameLength, dialect.maxColumnNameLength)
				},
				// Namespace prefix prefix for table names
				config.useNamespacePrefixForTableNames(),
				// integer IDs
				config.useIntegerIDs())
		return mapper.getMappedSchema()
	}

	public static AppSchema readApplicationSchema(String schemaLoc) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		GMLAppSchemaReader decoder = new GMLAppSchemaReader(null, null, schemaLoc)
		return decoder.extractAppSchema()
	}
}
