/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.merge

import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
import groovy.transform.CompileStatic

/**
 * Some utility methods related to alignment migration/merge.
 * 
 * @author Simon Templer
 */
@CompileStatic
class MergeUtil {

	/**
	 * Get information on the entity's filter as string.
	 * @param entity the entity to describe
	 * @return the description combined to a string
	 */
	static String getContextInfoString(EntityDefinition entity, String separator = ', ') {
		getContextInfo(entity).join(separator)
	}

	/**
	 * Get information on the entity's filter and contexts as a list of descriptions.
	 * @param entity the entity to describe
	 * @return the list of descriptions
	 */
	static List<String> getContextInfo(EntityDefinition entity) {
		List<String> result = []

		if (entity.filter) {
			def filterString = FilterDefinitionManager.instance.asString(entity.filter)
			if (filterString) {
				result << ("Filter \"${filterString}\" on type ${entity.type.displayName}" as String)
			}
		}

		if (entity.propertyPath) {
			List<String> props = [entity.type.displayName]
			for (ChildContext child : entity.propertyPath) {
				props << child.child.displayName

				if (child.condition?.filter) {
					def filterString = FilterDefinitionManager.instance.asString(child.condition.filter)
					if (filterString) {
						result << ("Condition \"${filterString}\" on property ${props.join('.')}" as String)
					}
				}
				if (child.index != null) {
					result << ("Index ${child.index} on property ${props.join('.')}" as String)
				}
			}
		}

		result
	}
}
