/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.helper;

import java.util.List;

import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Represents a path of definitions.
 * 
 * FIXME might have to be adapted to represent possible loops
 * 
 * @author Simon Templer
 */
public interface DefinitionPath {

	/**
	 * Get the path of definitions.
	 * 
	 * @return the list of definitions on the path
	 */
	public List<Definition<?>> getPath();

	/**
	 * Create a sub path.
	 * 
	 * @param child the child to add at the end of the path
	 * @return the child path
	 */
	public DefinitionPath subPath(Definition<?> child);

	/**
	 * Create a sub path.
	 * 
	 * @param append the path to append
	 * @return the child path
	 */
	public DefinitionPath subPath(DefinitionPath append);

}
