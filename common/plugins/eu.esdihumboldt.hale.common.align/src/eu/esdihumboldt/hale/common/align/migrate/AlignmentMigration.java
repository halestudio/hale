/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.migrate;

import java.util.Optional;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Interface describing an alignment migration.
 * 
 * @author Simon Templer
 */
public interface AlignmentMigration {

	/**
	 * Yields a replacement for an entity existing in a given alignment.
	 * 
	 * @param entity the entity to replace
	 * @return the replacement entity, if the entity should be replaced
	 */
	Optional<EntityDefinition> entityReplacement(EntityDefinition entity);

}
