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

import org.w3c.dom.Document

import eu.esdihumboldt.hale.io.codelist.inspire.reader.INSPIRECodeListReader
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.dom.DOMCategory

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
@CompileStatic
public class RegistryCodeLists {

	public static final URI CODE_LISTS = URI.create("http://inspire.ec.europa.eu/codelist/");

	public static Collection<CodeListRef> loadCodeLists() {
		parse(INSPIRECodeListReader.loadXmlDocument(CODE_LISTS))
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
						schemaName: codelist.applicationschema.label[0]?.text())
			}
		}
	}
}
