/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.cs3d.common.metamodel.helperGeometry.BoundingBox;
import de.fhg.igd.mapviewer.waypoints.GenericWaypoint;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Map way-point representing an {@link Instance}. 
 * @author Simon Templer
 */
public class InstanceWaypoint extends GenericWaypoint<InstanceReference, InstanceWaypoint> {

	private final List<GeometryProperty<?>> geometries;
	
	private final String name;

	/**
	 * Create an instance way-point.
	 * @param pos the way-point position
	 * @param bb the bounding box
	 * @param value the reference to the instance
	 * @param geometries the instance geometries
	 * @param name the instance name, <code>null</code> if unknown
	 */
	public InstanceWaypoint(GeoPosition pos, BoundingBox bb,
			InstanceReference value, List<GeometryProperty<?>> geometries,
			String name) {
		super(pos, bb, value);
		
		this.geometries = geometries;
		this.name = name;
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
	 * @return the instance name or <code>null</code> if unknown
	 */
	public String getName() {
		return name;
	}

}
