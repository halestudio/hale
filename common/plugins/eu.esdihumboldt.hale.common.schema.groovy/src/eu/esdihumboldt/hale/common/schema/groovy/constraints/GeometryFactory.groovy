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

import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType


/**
 * Factory for {@link GeometryType} constraint.
 * 
 * @author Simon Templer
 */
@Singleton
class GeometryFactory implements ConstraintFactory<GeometryType> {

	@Override
	public GeometryType createConstraint(Object arg, Definition<?> context = null) {
		TypeDefinition typeDef = context as TypeDefinition

		// set also the binding to GeometryProperty
		typeDef.setConstraint(eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding.get(GeometryProperty))

		new GeometryType(arg as Class, typeDef)
	}
}
