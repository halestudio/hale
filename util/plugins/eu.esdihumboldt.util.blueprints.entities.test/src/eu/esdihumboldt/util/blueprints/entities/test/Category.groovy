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


package eu.esdihumboldt.util.blueprints.entities.test

import eu.esdihumboldt.util.blueprints.entities.VertexEntity

/**
 * Category as sample entity.
 * 
 * @author Simon Templer
 */
@VertexEntity('category')
class Category {
	/**
	 * Category name
	 */
	String name

	/**
	 * Category description
	 */
	String description
}
