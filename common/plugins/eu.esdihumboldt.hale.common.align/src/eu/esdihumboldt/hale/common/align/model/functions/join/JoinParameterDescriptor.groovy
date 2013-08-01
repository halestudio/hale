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

package eu.esdihumboldt.hale.common.align.model.functions.join;

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.core.io.ComplexValueType

/**
 * Descriptor for conversion between DOM and JoinParameter.
 * 
 * @author Kai Schwierczek
 */
public class JoinParameterDescriptor implements ComplexValueType<JoinParameter> {

	// TODO

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#fromDOM(org.w3c.dom.Element)
	 */
	@Override
	public JoinParameter fromDOM(Element fragment) {
		return new JoinParameter();
		//		// retrieve values using DOMCategory
		//		use (DOMCategory) {
		//			return new TestAnnotation(author: fragment.'@author', comment: fragment.text())
		//		}
		//
		//
		//
		//		Map<Value, Value> values = new HashMap<Value, Value>();
		//
		//		use (DOMCategory) {
		//			for (entry in fragment.entry) {
		//				Value key = fromTag(entry.key[0]);
		//				Value value = fromTag(entry.value[0])
		//				values.put(key, value)
		//			}
		//		}
		//
		//		return new LookupTableImpl(values);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#toDOM(java.lang.Object)
	 */
	@Override
	public Element toDOM(JoinParameter value) {
		return null;
		//		// using DOMBuilder create the DOM structure from the annotation object
		//		def builder = DOMBuilder.newInstance()
		//		// w/o namespace
		//		//def fragment = builder.comment(author: annotation.author, annotation.comment)
		//		// with namespace (cleaner document)
		//		def fragment = builder.'test:comment'('xmlns:test': NS, author: annotation.author, annotation.comment)
		//
		//		return fragment;
		//
		//
		//
		//
		//		def builder = DOMBuilder.newInstance(false, true)
		//
		//		def valueTag = { String tagName, Value value ->
		//			if (value.isRepresentedAsDOM()) {
		//				def element = builder."$tagName"()
		//				def child = value.getDOMRepresentation();
		//				// add value representation as child
		//				element.appendChild(element.ownerDocument.adoptNode(child))
		//			}
		//			else {
		//				builder."$tagName"(value: value.stringRepresentation)
		//			}
		//		}
		//
		//		def fragment = builder.'lookup-table' {
		//			for (Value key in table.keys) {
		//				entry {
		//					valueTag('key', key)
		//					valueTag('value', table.lookup(key))
		//				}
		//			}
		//		}
		//
		//		return fragment;
	}
}
