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

package eu.esdihumboldt.hale.ui.codelist.service.internal.config;

import javax.xml.namespace.QName

import org.w3c.dom.Element

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap

import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory

/**
 * Converts {@link CodeListAssociations} configuration from DOM to object and back. 
 * 
 * @author Simon Templer
 */
public class CodeListAssociationsType implements ComplexValueType<CodeListAssociations, Void> {

	@Override
	public CodeListAssociations fromDOM(Element fragment, Void context) {
		CodeListAssociations result = new CodeListAssociations()

		use (DOMCategory) {
			for (codeList in fragment.codeList) {
				// create code list reference from codeList tag
				CodeListReference ref = new CodeListReference(codeList.'@namespace', codeList.'@identifier')

				codeList.entity.each {
					// collect names
					List<QName> names = []

					it.'*'.each {
						QName name = new QName(it.'@namespace', it.'@name')
						names << name
					}

					// create association
					DummyEntityKey key = new DummyEntityKey(names)
					result.associations.put(key, ref)
				}
			}
		}

		return result;
	}

	@Override
	public Element toDOM(CodeListAssociations value) {
		def builder = DOMBuilder.newInstance(false, true)

		// build multi map reverse to association map
		Multimap<CodeListReference, DummyEntityKey> reversed = HashMultimap.create()
		value.associations.each { key, val ->
			reversed.put(val, key)
		}

		def fragment = builder.'associations' {
			for (CodeListReference clRef in reversed.keySet()) {
				codeList(namespace: clRef.namespace, identifier: clRef.identifier) {
					reversed.get(clRef).each { DummyEntityKey key ->
						entity {
							key.names.eachWithIndex { QName name, idx ->
								if (idx == 0) {
									type(namespace: name.namespaceURI, name: name.localPart)
								}
								else {
									property(namespace: name.namespaceURI, name: name.localPart)
								}
							}
						}
					}
				}
			}
		}

		return fragment;
	}

	@Override
	public Class<Void> getContextType() {
		return Void
	}
}
