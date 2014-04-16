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

package eu.esdihumboldt.hale.ui.codelist.inspire.internal;

import java.lang.ref.SoftReference

import org.w3c.dom.Document

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.io.codelist.inspire.reader.INSPIRECodeListReader
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.dom.DOMCategory

/**
 * Provides access to code lists in INSPIRE registry.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class RegistryCodeLists {

	public static final URI CODE_LISTS = URI.create("http://inspire.ec.europa.eu/codelist/");

	private static final ALogger log = ALoggerFactory.getLogger(RegistryCodeLists)

	/**
	 * Caches the last loaded result.
	 */
	private static SoftReference<Collection<CodeListRef>> cached

	/**
	 * Load the code lists from the registry, may access an already cached list. 
	 */
	public static Collection<CodeListRef> loadCodeLists() {
		synchronized (RegistryCodeLists) {
			def result = cached?.get()
			if (result == null) {
				result = parse(INSPIRECodeListReader.loadXmlDocument(CODE_LISTS))
				cached = new SoftReference<Collection<CodeListRef>>(result)
			}
			result
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private static Collection<CodeListRef> parse(Document doc) {
		def register = doc.documentElement
		use (DOMCategory) {
			register.containeditems.codelist.collect { codelist ->
				new CodeListRef(
						name: codelist.label[0]?.text(),
						location: URI.create(codelist.'@id'),
						description: codelist.description[0]?.text(),
						definition: codelist.definition[0]?.text(),
						schemaName: codelist.applicationschema.label[0]?.text(),
						themeName: codelist.theme.label[0]?.text())
			}
		}
	}
}
