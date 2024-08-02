/*
 * Copyright (c) 2024 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.align.helper

import java.util.function.Function

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType

/**
 * Helper for identifying geometry properties
 * 
 * @author Simon Templer
 */
class GeometryPropertyFinder {

	static QName findGeometryProperty(TypeDefinition type, Function<TypeDefinition, Collection<? extends PropertyDefinition>> propertyFinder, String preferredName, SimpleLog log) {
		// allow for geometry property types with choices
		int checkLevels = 3

		// create finder for geometry properties
		EntityFinder finder = new EntityFinder({ EntityDefinition entity ->
			// determine if the property classifies as
			if (entity.getDefinition() instanceof PropertyDefinition) {
				def propertyType = ((PropertyDefinition) entity.getDefinition()).getPropertyType()

				boolean isGeometry = propertyType.getConstraint(GeometryType).isGeometry()
				if (isGeometry) {
					return true
				}
			}

			false
		}, checkLevels)

		def parents = propertyFinder.apply(type).collect { PropertyDefinition p ->
			AlignmentUtil.createEntityFromDefinitions(type, [p], SchemaSpaceID.SOURCE, null)
		}

		def candidates = finder.find(parents)

		if (candidates.empty) {
			null
		}
		else {
			// select candidate

			// extract main property names; order matters because of traversal order for finding the candidates
			Set<QName> names = new LinkedHashSet(candidates*.propertyPath[0]*.child*.name)

			// prefer geometry column name w/o namespace
			def preferred = new QName(preferredName)
			if (names.contains(preferred)) {
				return preferred
			}

			// otherwise prefer any with geometry column name
			preferred = names.find { preferredName == it.localPart }

			if (preferred == null) {
				// otherwise use first one
				preferred = names.iterator().next()
			}

			log.info("Identified property $preferred as geometry property for type ${type.name.localPart}")

			preferred
		}
	}
}
