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

package eu.esdihumboldt.hale.common.align.migrate

import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * Type representing an entity match in an alignment migration with additional information.
 * 
 * @author Simon Templer
 */
@CompileStatic
@Immutable(knownImmutableClasses = [EntityDefinition.class])
class EntityMatch {
	EntityDefinition match
	boolean multipleCandidates
	boolean matchPartOfJoin

	/**
	 * Create a copy of the match replacing the matched entity.
	 * 
	 * @param newMatch the entity to use as new match
	 * @return the copy of the match with the replaced entity
	 */
	EntityMatch withMatch(EntityDefinition newMatch) {
		return new EntityMatch(newMatch, multipleCandidates, matchPartOfJoin)
	}

	/**
	 * Create an entity match using the provided entity with default values assuming
	 * there is only this candidate and there is no join involved.
	 * 
	 * @param match the entity to use for the match
	 * @return the entity match using the provided entity
	 */
	static EntityMatch of(EntityDefinition match) {
		return new EntityMatch(match, false, false)
	}
}
