/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.waypoints.GenericWaypoint;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Map way-point representing an {@link Instance}.
 * 
 * @author Simon Templer
 */
public class InstanceWaypoint extends GenericWaypoint<InstanceReference, InstanceWaypoint> {

	private final List<GeometryProperty<?>> geometries;

	private final String name;

	private final TypeDefinition instanceType;

	/**
	 * Create an instance way-point.
	 * 
	 * @param pos the way-point position
	 * @param bb the bounding box
	 * @param value the reference to the instance
	 * @param geometries the instance geometries
	 * @param instanceType the type definition associated with the instance
	 * @param name the instance name, <code>null</code> if unknown
	 */
	public InstanceWaypoint(GeoPosition pos, BoundingBox bb, InstanceReference value,
			List<GeometryProperty<?>> geometries, TypeDefinition instanceType, String name) {
		super(pos, bb, value);

		this.geometries = geometries;
		this.name = name;
		this.instanceType = instanceType;
	}

	/**
	 * @return the geometries
	 */
	public List<GeometryProperty<?>> getGeometries() {
		return geometries;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * Get the instance name.
	 * 
	 * @return the instance name or <code>null</code> if unknown
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the instance type definition
	 */
	public TypeDefinition getInstanceType() {
		return instanceType;
	}

}
