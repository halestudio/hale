/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.helper.spec

import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionArgument
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionSpecification
import eu.esdihumboldt.util.groovy.builder.BuilderBase
import groovy.transform.CompileStatic


/**
 * Builder for helper function specifications.
 * @author Simon Templer
 */
@CompileStatic
class SpecBuilder extends BuilderBase {

	public static Specification newSpec(Map attributes, Closure args = null) {
		def argList = [attributes]
		if (args != null) {
			argList << args
		}
		(Specification) new SpecBuilder().createNode('', argList)
	}

	@Override
	protected Object internalCreateNode(String name, Map attributes, List params, Object parent,
			boolean subClosure) {
		Object node = null

		if (parent == null) {
			// create a specification
			HelperFunctionSpecification spec = new HelperFunctionSpecification()

			if (attributes) {
				if (attributes.description) {
					spec.description = attributes.description
				}

				if (attributes.result || attributes.resultDescription) {
					spec.resultDescription = attributes.result ?: attributes.resultDescription
				}
			}

			node = spec
		}
		else if (parent instanceof HelperFunctionSpecification) {
			// create an argument
			HelperFunctionArgument arg = new HelperFunctionArgument()

			arg.name = name

			if (attributes && attributes.description) {
				arg.description = attributes.description
			}
			else if (params) {
				// otherwise first argument must be the description
				arg.description = params[0]
			}

			if (attributes && attributes.value != null) {
				// default value
				arg.defaultValue = attributes.value
			}

			parent.addArgument(arg)

			node = arg
		}
		else {
			// TODO throw some kind of exception?
			throw new IllegalStateException()
		}

		node
	}
}
