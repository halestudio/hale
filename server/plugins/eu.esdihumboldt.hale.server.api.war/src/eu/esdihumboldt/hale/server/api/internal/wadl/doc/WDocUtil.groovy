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

package eu.esdihumboldt.hale.server.api.internal.wadl.doc

import java.lang.reflect.Method

import eu.esdihumboldt.hale.server.api.internal.wadl.generated.WadlDoc


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
	 * @return the annotations or an empty list
	 */
	static final def getDocs(Method method, DocScope scope) {
		// try directly getting the WadlDoc
		WDoc doc = method.getAnnotation(WDoc)
		if (doc && doc.scope() == scope) {
			return [doc]
		}

		// try WadlDocs
		def result = []
		WDocs docs = method.getAnnotation(WDocs)
		if (docs) {
			for (WDoc edoc in docs.value()) {
				if (edoc.scope() == scope) {
					result << edoc
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
	 * @return the annotations or an empty list
	 */
	static final def getDocs(Collection<Method> methods, DocScope scope) {
		// collect docs
		def docs = []
		methods.each { docs.addAll(getDocs(it, scope)) }

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
	 * @return the documentation objects or an empty list
	 */
	static final List<WadlDoc> getWadlDocs(Collection<Method> methods, DocScope scope) {
		getDocs(methods, scope).collect { toWadlDoc(it) }
	}

	/**
	 * Convert a WADL documentation annotation to a JAXB object.
	 * 
	 * @param wdoc the annotation
	 * @return the JAXB WADL documentation object 
	 */
	static final WadlDoc toWadlDoc(WDoc wdoc) {
		WadlDoc doc = new WadlDoc()

		doc.title = wdoc.title()
		if (wdoc.lang()) {
			doc.lang = wdoc.lang()
		}

		def closure = wdoc.content().newInstance(null, null)
		//TODO what parameters should be passed in here?
		def content = closure.call()

		//TODO support HTML and stuff
		doc.content << content

		return doc
	}
}
