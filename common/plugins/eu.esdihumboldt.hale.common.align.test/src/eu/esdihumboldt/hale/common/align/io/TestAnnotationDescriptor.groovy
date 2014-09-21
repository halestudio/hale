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

package eu.esdihumboldt.hale.common.align.io

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory


/**
 * Annotation descriptor for test purposes only.<br>
 * <br>
 * Annotation markup looks like this:<br>
 * <code>&lt;comment author=&quot;Name&quot;&gt;Text&lt;/comment&gt;</code>
 * 
 * @author Simon Templer
 */
class TestAnnotationDescriptor implements AnnotationDescriptor<TestAnnotation> {

	private static final String NS = 'http://www.esdi-humboldt.eu/hale/test'

	@Override
	TestAnnotation create() {
		return new TestAnnotation()
	}

	@Override
	TestAnnotation fromDOM(Element fragment, Void context) {
		// retrieve values using DOMCategory
		use (DOMCategory) {
			return new TestAnnotation(author: fragment.'@author', comment: fragment.text())
		}
	}

	@Override
	Element toDOM(TestAnnotation annotation) {
		// using DOMBuilder create the DOM structure from the annotation object
		def builder = DOMBuilder.newInstance()
		// w/o namespace
		//def fragment = builder.comment(author: annotation.author, annotation.comment)
		// with namespace (cleaner document)
		def fragment = builder.'test:comment'('xmlns:test': NS, author: annotation.author, annotation.comment)

		return fragment;
	}

	@Override
	public Class<Void> getContextType() {
		Void
	}
}
