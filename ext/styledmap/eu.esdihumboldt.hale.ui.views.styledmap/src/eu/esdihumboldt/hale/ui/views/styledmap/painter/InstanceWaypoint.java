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

import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.cs3d.common.metamodel.helperGeometry.BoundingBox;
import de.fhg.igd.mapviewer.waypoints.GenericWaypoint;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class InstanceWaypoint extends GenericWaypoint<InstanceReference, InstanceWaypoint> {

	/**
	 * @param pos
	 * @param bb
	 * @param value
	 */
	public InstanceWaypoint(GeoPosition pos, BoundingBox bb,
			InstanceReference value) {
		super(pos, bb, value);
		// TODO Auto-generated constructor stub
	}

}
