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

package eu.esdihumboldt.hale.common.align.io;

import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Context for loading an alignment. Can be used as context for complex values
 * stored in an alignment.
 * 
 * @author Simon Templer
 */
public interface LoadAlignmentContext {

	/**
	 * @return the source schema type index
	 */
	public TypeIndex getSourceTypes();

	/**
	 * @return the target schema type index
	 */
	public TypeIndex getTargetTypes();

}
