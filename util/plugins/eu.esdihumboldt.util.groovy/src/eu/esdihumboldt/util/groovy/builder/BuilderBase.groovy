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

package eu.esdihumboldt.util.groovy.builder

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode

import org.codehaus.groovy.runtime.InvokerHelper



/**
 * Builder base class that supports creating nodes dependent on the parent node.
 * 
 * @author Simon Templer
 */
@CompileStatic
abstract class BuilderBase {

	/**
	 * The current node.
	 */
	def current

	/**
	 * Reset the builder
	 */
	void reset() {
		current = null
	}

	/**
	 * Called on for any missing method.
	 * 
	 * @param name the method name
	 * @param args the arguments
	 * @return something
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	@TypeChecked
	def methodMissing(String name, def args) {
		List list = InvokerHelper.asList(args)

		// determine named parameters (must be first)
		Map attributes = null
		def start = 0
		if (list && list[0] instanceof Map) {
			attributes = (Map) list[0]
			start = 1
		}

		// determine closure (must be last)
		def end = list.size()
		Closure closure = null
		if (list && list.last() instanceof Closure) {
			closure = (Closure) list.last().clone()
			closure.delegate = this
			end--
		}

		// determine other parameters
		List params = null
		if (start < end) {
			params = list.subList(start, end)
		}

		def parent = current
		def node = createNode(name, attributes, params, parent, closure != null)
		current = node

		closure?.call()

		current = parent

		// return the node created by the call
		node
	}

	/**
	 * Create a new node.
	 * 
	 * @param name the node name
	 * @param attributes the named parameters, may be <code>null</code>
	 * @param params other parameters, may be <code>null</code>
	 * @param parent the parent node, may be <code>null</code>
	 * @param subClosure states if there is a sub-closure for this node
	 * @return the created node
	 */
	def abstract createNode(String name, Map attributes, List params, def parent, boolean subClosure);

}
