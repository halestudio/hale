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

package eu.esdihumboldt.hale.io.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.json.writer.InstanceToJson;

/**
 * Object holding information about default geometries of types to use for the
 * GeoJSON export.
 * 
 * @author Kai Schwierczek
 * 
 * @deprecated as of release 4.2.0 this class is deprecated because
 *             {@link InstanceToJson} is used to export the data into GeoJson or
 *             Json format.
 */
@Deprecated
public class GeoJSONConfig {

	private final Map<TypeDefinition, PropertyEntityDefinition> defaultGeometries = new HashMap<>();

	/**
	 * Add a default geometry property.<br>
	 * Each type may only have one property set, a subsequent call with the same
	 * type overrides the previous default. The property does not directly need
	 * to be the geometry, any geometries below the given property will be
	 * found.
	 * 
	 * @param type the type to set the default geometry property for
	 * @param property the default geometry property for the given type (may be
	 *            <code>null</code>)
	 */
	public void addDefaultGeometry(TypeDefinition type, PropertyEntityDefinition property) {
		defaultGeometries.put(type, property);
	}

	/**
	 * Returns the default geometry property for the given type.<br>
	 * Geometries must be searched below the given property.
	 * 
	 * @param type the type in question
	 * @return the default geometry property for the given type (may be
	 *         <code>null</code>)
	 */
	public PropertyEntityDefinition getDefaultGeometry(TypeDefinition type) {
		return defaultGeometries.get(type);
	}

	/**
	 * @return the default geometries configuration map
	 */
	public Map<TypeDefinition, PropertyEntityDefinition> getDefaultGeometries() {
		return Collections.unmodifiableMap(defaultGeometries);
	}
}
