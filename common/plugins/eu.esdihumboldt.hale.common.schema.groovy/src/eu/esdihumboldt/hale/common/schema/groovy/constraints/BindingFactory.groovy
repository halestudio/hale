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

package eu.esdihumboldt.hale.common.schema.groovy.constraints

import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding
import groovy.transform.CompileStatic


/**
 * Factory for binding constraints. Expects a class or something that can be
 * converted to a class as argument.
 * 
 * @author Simon Templer
 */
@Singleton
@CompileStatic
class BindingFactory extends OptionalContextConstraintFactory<Binding> {

	@Override
	Binding createConstraint(Object arg, Definition context) {
		Binding.get(arg as Class)
	}
}
