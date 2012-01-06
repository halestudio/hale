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

import java.util.Collection;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.mapviewer.AbstractTileOverlayPainter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.ui.geometry.GeometryUtil;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Painter for source instances.
 * @author Simon Templer
 */
public class SourceInstancePainter extends AbstractInstancePainter {

	/**
	 * Default constructor
	 */
	public SourceInstancePainter() {
		super((InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class), DataSet.SOURCE);
	}

	/**
	 * @see AbstractInstancePainter#createWaypoint(Instance, InstanceService)
	 */
	@Override
	protected InstanceWaypoint createWaypoint(Instance instance,
			InstanceService instanceService) {
		// retrieve instance reference
		InstanceReference ref = instanceService.getReference(instance, getDataSet());
		
		Collection<GeometryProperty<?>> geometries = GeometryUtil.getDefaultGeometries(instance);
		
		//TODO determine bounding box
		
		//TODO determine geo-position
		
		//XXX convert BB to waypoint SRS?
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see AbstractTileOverlayPainter#getMaxOverlap()
	 */
	@Override
	protected int getMaxOverlap() {
		return 12;
	}

}
