/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.instance.index;

import java.util.Collection;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Interface for transformation functions that use an {@link InstanceIndex} and
 * require instance properties to be indexed.
 * 
 * @author Florian Esser
 */
public interface InstanceIndexContribution {

	/**
	 * Provides a collection of groups of properties that need to be included in
	 * the instance index. Every property group (i.e. the properties grouped
	 * together in the inner lists) form a combined key in the index.
	 * 
	 * @param cell Cell to analyze
	 * @return The property groups to be indexed
	 */
	Collection<List<PropertyEntityDefinition>> getIndexContribution(Cell cell);
}
