/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.functions.bgis.sourcedesc;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Common utility methods for source description function.
 * 
 * @author Simon Templer
 */
public class SourceDescriptionUtil {

	/**
	 * Derive a source description from a type cell.
	 * 
	 * @param typeCell the type cell
	 * @return the source description or <code>null</code>
	 */
	public static String getSourceDescription(Cell typeCell) {
		ListMultimap<String, ? extends Entity> source = typeCell.getSource();

		if (source != null && !source.isEmpty()) {
			StringBuilder builder = new StringBuilder();

			builder.append('[');
			boolean first = true;
			for (Entity entity : source.values()) {
				if (first)
					first = false;
				else
					builder.append(", ");
				builder.append(entity.getDefinition().getDefinition().getDisplayName());
			}
			builder.append(']');

			return builder.toString();
		}

		return null;
	}
}
