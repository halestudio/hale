/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.inspire.schemas

import java.lang.ref.SoftReference

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * Provides information on INSPIRE application schemas.
 * 
 * @author Simon Templer
 */
public class ApplicationSchemas {

	public static final URI APP_SCHEMA_ASSOCIATIONS = URI.create("resource://inspire.ec.europa.eu/applicationSchemas.xml");

	private static final ALogger log = ALoggerFactory.getLogger(ApplicationSchemas)

	/**
	 * Caches the last loaded result.
	 */
	private static SoftReference<Multimap<String, SchemaInfo>> cached

	/**
	 * Load the schema information, may access an already cached lists.
	 * 
	 * @return schema namespaces mapped to schema information
	 */
	static Multimap<String, SchemaInfo> getSchemaInfos() {
		synchronized (ApplicationSchemas) {
			def result = cached?.get()
			if (result == null) {
				result = loadSchemaInfos();
				cached = new SoftReference<Multimap<String, SchemaInfo>>(result)
			}
			result
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private static Multimap<String, SchemaInfo> loadSchemaInfos() {
		DefaultInputSupplier is = new DefaultInputSupplier(APP_SCHEMA_ASSOCIATIONS)
		def xml
		is.getInput().withStream {
			xml = new XmlSlurper().parse(it)
		}

		def result = ArrayListMultimap.create()
		xml.applicationSchema.each { appSchema ->
			String id = appSchema.@id as String

			// load application schema information
			//			Document doc = INSPIRECodeListReader.loadXmlDocument(URI.create(id))
			//			def applicationschema = doc.documentElement
			//			def name
			//			use (DOMCategory) {
			//				name = applicationschema.label.text()
			//			}
			String name = appSchema.@name

			// create schema info for each schema
			appSchema.schema.each { schema ->
				def location = URI.create(schema.@url as String)

				// load schema
				DefaultInputSupplier schemaIs = new DefaultInputSupplier(location)
				def xsd
				schemaIs.getInput().withStream {
					xsd = new XmlSlurper().parse(it)
				}
				def namespace = xsd.@targetNamespace as String
				def directImports = []
				xsd.'import'.each {
					def ns = (it.@namespace as String)
					if (ns.toLowerCase().contains('inspire')) {
						// only record INSPIRE related imports
						directImports << ns
					}
				}

				SchemaInfo schemaInfo = new SchemaInfo(
						location: location,
						version: schema.@version as String,
						namespace: namespace,
						appSchemaId: id,
						name: name,
						directImports: directImports
						)
				result.put(namespace, schemaInfo)
			}
		}

		result
	}
}
