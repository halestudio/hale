/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.wadl.doc

import java.lang.reflect.Method

import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Element

import eu.esdihumboldt.hale.server.api.wadl.internal.generated.WadlDoc


/**
 * Utility methods for dealing with {@link WDoc}.
 * 
 * @author Simon Templer
 */
class WDocUtil {

	/**
	 * Get the WADL documentation annotation with the given scope from a method.
	 * 
	 * @param method the method declaration
	 * @param scope the documentation scope
	 * @param context the scope context, may be <code>null</code>
	 * @return the annotations or an empty list
	 */
	static final def getDocs(Method method, DocScope scope, String context) {
		// try directly getting the WadlDoc
		WDoc doc = method.getAnnotation(WDoc)
		if (doc && doc.scope() == scope) {
			// scope must match
			if (!context || doc.context() == context) {
				// and context if given
				return [doc]
			}
		}

		// try WadlDocs
		def result = []
		WDocs docs = method.getAnnotation(WDocs)
		if (docs) {
			for (WDoc edoc in docs.value()) {
				if (edoc.scope() == scope) {
					// scope must match
					if (!context || edoc.context() == context) {
						// and context if given
						result << edoc
					}
				}
			}
		}

		return result
	}

	/**
	 * Get the WADL documentation annotation with the given scope from a set
	 * of methods.
	 *
	 * @param methods the method declarations
	 * @param scope the documentation scope
	 * @param context the scope context, may be <code>null</code>
	 * @return the annotations or an empty list
	 */
	static final def getDocs(Collection<Method> methods, DocScope scope, String context) {
		// collect docs
		def docs = []
		methods.each { docs.addAll(getDocs(it, scope, context)) }

		/*
		 * TODO organize by language and don't pass through all?
		 * XXX candidates could be selected by an additionally specified
		 * priority or being merged 
		 */

		return docs
	}

	/**
	 * Get the WADL documentation objects with the given scope from a set
	 * of methods.
	 *
	 * @param methods the method declarations
	 * @param scope the documentation scope
	 * @param context the scope context, may be <code>null</code>
	 * @param baseURI the baseURI representing the webapp context
	 * @return the documentation objects or an empty list
	 */
	static final List<WadlDoc> getWadlDocs(Collection<Method> methods, DocScope scope,
			String context, String baseURI) {
		getDocs(methods, scope, context).collect { toWadlDoc(it, baseURI) }
	}

	/**
	 * Convert a WADL documentation annotation to a JAXB object.
	 * 
	 * @param wdoc the annotation
	 * @return the JAXB WADL documentation object 
	 */
	static final WadlDoc toWadlDoc(WDoc wdoc, String baseURI) {
		WadlDoc doc = new WadlDoc()

		doc.title = wdoc.title()
		if (wdoc.lang()) {
			doc.lang = wdoc.lang()
		}

		def closure = wdoc.content().newInstance(null, null)
		//TODO what parameters should be passed in here?
		def content = closure.call(baseURI)

		//TODO support HTML and stuff
		doc.content << content

		return doc
	}

	/**
	 * Interpret a string as XHTML for use in {@link WDoc} content.
	 * The supplied XHTML will be wrapped in a <code>div</code>.
	 * 
	 * @param html the XHTML fragment
	 * @return the XHTML div element wrapping the XHTML string
	 */
	static final Element xhtml(String html) {
		// wrap html string in div
		html = "<div xmlns=\"http://www.w3.org/1999/xhtml\">${html}</div>"

		def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		def input = new ByteArrayInputStream(html.getBytes('UTF-8'))
		return builder.parse(input).documentElement
	}
}
