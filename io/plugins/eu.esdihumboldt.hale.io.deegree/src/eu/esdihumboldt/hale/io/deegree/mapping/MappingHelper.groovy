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

import javax.xml.stream.FactoryConfigurationError
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter

import org.deegree.commons.xml.stax.IndentingXMLStreamWriter
import org.deegree.cs.coordinatesystems.ICRS
import org.deegree.cs.persistence.CRSManager
import org.deegree.feature.persistence.sql.GeometryStorageParams
import org.deegree.feature.persistence.sql.MappedAppSchema
import org.deegree.feature.persistence.sql.config.SQLFeatureStoreConfigWriter
import org.deegree.feature.persistence.sql.ddl.DDLCreator
import org.deegree.feature.persistence.sql.mapper.AppSchemaMapper
import org.deegree.feature.types.AppSchema
import org.deegree.gml.schema.GMLAppSchemaReader
import org.deegree.sqldialect.SQLDialect
import org.deegree.sqldialect.postgis.PostGISDialect

import groovy.transform.CompileStatic

/**
 * Helper class to generate DDL and deegree FeatureStore configuration files based on GML schema.
 */
@CompileStatic
class MappingHelper {

	/**
	 * Generate a feature store configuration and DDL.
	 * @param  schemaLoc    the location of the GML Application Schema
	 * @param  srs          the SRS code, e.g. EPSG:4326
	 * @param  connectionId the connection ID of the deegree database connection
	 * @param  blobMapping  if a blob mapping should be created
	 * @param  featureStore the file to write the feature store config to
	 * @param  ddl          the file to write the DDL to
	 * @return  the list of feature types
	 */
	static List generateConfig(String schemaLoc, String srs, String connectionId, boolean blobMapping,
			File featureStore, File ddl) throws Exception {
		SQLDialect dialect = new PostGISDialect('2.0')
		List<String> schemaUrls = [schemaLoc]

		ICRS crs = CRSManager.getCRSRef(srs ?: "EPSG:4326")

		AppSchema appSchema = readApplicationSchema(schemaLoc)
		MappedAppSchema mappedSchema = mapApplicationSchema(dialect, appSchema, crs, blobMapping)

		if (featureStore) {
			writeConfig(mappedSchema, schemaUrls, connectionId, featureStore)
		}
		if (ddl) {
			writeDdl(mappedSchema, dialect, ddl)
		}

		def types = appSchema.getFeatureTypes(null, false, false).toList()
		types.collect {
			[name: it.name.localPart, namespace: it.name.namespaceURI]
		}
	}

	private static MappedAppSchema mapApplicationSchema(SQLDialect dialect,
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

	private static AppSchema readApplicationSchema(String schemaLoc) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		GMLAppSchemaReader decoder = new GMLAppSchemaReader(null, null, schemaLoc)
		return decoder.extractAppSchema()
	}

	private static void writeDdl(MappedAppSchema mappedSchema, SQLDialect dialect, File target) {
		def ddl = DDLCreator.newInstance( mappedSchema, dialect ).getDDL().toList()
		ddl << ''
		target.text = ddl.join(';\n')
	}

	private static void writeConfig(MappedAppSchema mappedSchema, List<String> schemaUrls,
			String connectionId, File target) throws IOException, FileNotFoundException, XMLStreamException, FactoryConfigurationError {
		//XXX also takes properties with href primitive mappings
		SQLFeatureStoreConfigWriter configWriter = new SQLFeatureStoreConfigWriter(mappedSchema)

		target.withOutputStream {
			XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(it)
			xmlWriter = new IndentingXMLStreamWriter(xmlWriter)
			try {
				configWriter.writeConfig(xmlWriter, connectionId, schemaUrls)
			} finally {
				xmlWriter.close()
			}
		}
	}
}
