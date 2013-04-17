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

package eu.esdihumboldt.hale.functions.bgis.capturespec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.CityGMLConstants;

/**
 * Common utility methods for data capture specification function.
 * 
 * @author Simon Templer
 */
public class DataCaptureSpecUtil {

	/**
	 * Extract the data capture specification from a type cell.
	 * 
	 * @param typeCell the type cell
	 * @return the data capture specification or <code>null</code>
	 */
	public static String getDataCaptureSpec(Cell typeCell) {
		ListMultimap<String, ? extends Entity> source = typeCell.getSource();

		if (source != null && !source.isEmpty()) {
			for (Entity entity : source.values()) {
				Definition<?> def = entity.getDefinition().getDefinition();
				if (def instanceof TypeDefinition) {
					String ns = def.getName().getNamespaceURI();
					if (ns != null && ns.startsWith(CityGMLConstants.CITYGML_NAMESPACE_CORE)) {
						// extract version from namespace
						Pattern pattern = Pattern.compile("[^/]*/([1-9][^/]*)");
						Matcher matcher = pattern.matcher(ns);

						if (matcher.find()) {
							return "CityGML " + ns.substring(matcher.start(1), matcher.end(1));
						}
					}
				}
			}
		}

		return null;
	}
}
