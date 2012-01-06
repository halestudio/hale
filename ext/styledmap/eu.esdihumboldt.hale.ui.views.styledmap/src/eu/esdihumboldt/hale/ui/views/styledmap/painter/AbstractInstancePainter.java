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

import de.fhg.igd.mapviewer.waypoints.CustomWaypointPainter;
import de.fhg.igd.mapviewer.waypoints.GenericWaypointPainter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;

/**
 * Abstract instance painter implementation based on an {@link InstanceService}.
 * @author Simon Templer
 */
public abstract class AbstractInstancePainter extends
		GenericWaypointPainter<InstanceReference, InstanceWaypoint> implements InstanceServiceListener {

	private final InstanceService instanceService;
	
	private final DataSet dataSet;

	/**
	 * Create an instance painter.
	 * @param instanceService the instance service
	 * @param dataSet the data set
	 */
	public AbstractInstancePainter(InstanceService instanceService,
			DataSet dataSet) {
		super();
		this.instanceService = instanceService;
		this.dataSet = dataSet;
		
		instanceService.addListener(this); //XXX instead only install when visible and active?!
	}

	/**
	 * @see InstanceServiceListener#datasetChanged(DataSet)
	 */
	@Override
	public void datasetChanged(DataSet type) {
		if (type == dataSet) {
			resetWaypoints();
		}
	}

	/**
	 * Reset way-points to those currently in the instance service.
	 */
	private void resetWaypoints() {
		clearWaypoints();
		
		//XXX only mappable type instances for source?!
		InstanceCollection instances = instanceService.getInstances(dataSet);
		
		// add way-points for instances 
		ResourceIterator<Instance> it = instances.iterator();
		Refresher refresh = prepareRefresh();
		try {
			while (it.hasNext()) {
				Instance instance = it.next();
				
				InstanceWaypoint wp = createWaypoint(instance, instanceService);
				
				addWaypoint(wp, refresh);
			}
		} finally {
			it.close();
			refresh.execute();
		}
	}

	/**
	 * Create a way-point for an instance
	 * @param instance the instance
	 * @param instanceService the instance service
	 * @return the created way-point
	 */
	protected abstract InstanceWaypoint createWaypoint(Instance instance,
			InstanceService instanceService);

	/**
	 * @return the instance service
	 */
	public InstanceService getInstanceService() {
		return instanceService;
	}

	/**
	 * @return the data set
	 */
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @see CustomWaypointPainter#dispose()
	 */
	@Override
	public void dispose() {
		instanceService.removeListener(this); //XXX instead only install when visible and active?!
		
		super.dispose();
	}
	
}
