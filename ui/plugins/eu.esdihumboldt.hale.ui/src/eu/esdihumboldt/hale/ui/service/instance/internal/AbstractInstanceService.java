/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.service.instance.internal;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;

/**
 * Notification handling for {@link InstanceService}s that support
 * {@link InstanceServiceListener}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractInstanceService implements InstanceService {
	
	private TypeSafeListenerList<InstanceServiceListener> listeners = new TypeSafeListenerList<InstanceServiceListener>();
	
	private final AlignmentService alignmentService;
	private final ProjectService projectService;
	
	private boolean liveTransform = true; //TODO where to store the configuration? project?
	
	/**
	 * Create an instance service.
	 * @param projectService the project service. The instances will be cleared
	 *   when the project is cleaned.
	 * @param alignmentService the alignment service
	 */
	public AbstractInstanceService(ProjectService projectService,
			AlignmentService alignmentService) {
		super();
		
		this.alignmentService = alignmentService;
		this.projectService = projectService;
		
		projectService.addListener(new ProjectServiceAdapter() {
			
			@Override
			public void onClean() {
				clearInstances();
			}
			
		});
		
		alignmentService.addListener(new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				clearTransformedInstances();
			}

			@Override
			public void cellRemoved(Cell cell) {
				/*
				 * TODO analyze cell if it is a type or property mapping
				 * property mapping: retransform based on related type mappings
				 * type mapping: removed transformed instances based on type
				 *   mapping
				 */
				retransform();
			}

			@Override
			public void cellReplaced(Cell oldCell, Cell newCell) {
				/*
				 * TODO only retransform with relevant cells (i.e. create a 
				 * view on the alignment) 
				 */
				retransform();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				/*
				 * TODO only retransform with relevant cells (i.e. create a 
				 * view on the alignment) 
				 */
				retransform();
			}
		});
	}

	/**
	 * @see InstanceService#setTransformationEnabled(boolean)
	 */
	@Override
	public void setTransformationEnabled(boolean enabled) {
		if (enabled != liveTransform) {
			liveTransform = enabled; //XXX use a lock for liveTransform?
			if (enabled) {
				retransform();
			}
			else {
				clearTransformedInstances();
			}
			notifyTransformationToggled(enabled);
		}
	}

	/**
	 * @see InstanceService#isTransformationEnabled()
	 */
	@Override
	public boolean isTransformationEnabled() {
		return liveTransform;
	}

	/**
	 * Retransform all instances. Decides if a transformation should be done
	 * or not.
	 */
	protected final void retransform() {
		if (isTransformationEnabled()) {
			doRetransform();
		}
	}
	
	/**
	 * Retransform all instances.
	 */
	protected abstract void doRetransform();

	/**
	 * Clear the transformed instances
	 */
	protected abstract void clearTransformedInstances();

	/**
	 * @return the transformationService
	 */
	protected TransformationService getTransformationService() {
		return OsgiUtils.getService(TransformationService.class);
	}

	/**
	 * @return the alignmentService
	 */
	protected AlignmentService getAlignmentService() {
		return alignmentService;
	}

	/**
	 * @return the projectService
	 */
	protected ProjectService getProjectService() {
		return projectService;
	}
	
	/**
	 * Called when the transformation has been enabled or disabled.
	 * @param enabled if the transformation is enabled now
	 */
	public void notifyTransformationToggled(boolean enabled) {
		for (InstanceServiceListener listener : listeners) {
			listener.transformationToggled(enabled);
		}
	}

	/**
	 * Notify listeners that a data set has changed
	 * 
	 * @param type the data set type, <code>null</code> if both sets have changed
	 */
	protected void notifyDatasetChanged(DataSet type) {
		for (InstanceServiceListener listener : listeners) {
			if (type == null) {
				listener.datasetChanged(DataSet.SOURCE);
				listener.datasetChanged(DataSet.TRANSFORMED);
			} else {
				listener.datasetChanged(type);
			}
		}
	}

	/**
	 * Notify listeners that a data set is about to be changed
	 * 
	 * @param type the data set type, <code>null</code> if both sets will change
	 */
	protected void notifyDatasetAboutToChange(DataSet type) {
		for (InstanceServiceListener listener : listeners) {
			if (type == null) {
				listener.datasetAboutToChange(DataSet.SOURCE);
				listener.datasetAboutToChange(DataSet.TRANSFORMED);
			} else {
				listener.datasetAboutToChange(type);
			}
		}
	}
	
//	/**
//	 * Notify listeners that the CRS has changed
//	 * 
//	 * @param crs the new CRS definition
//	 */
//	protected void notifyCRSChanged(CRSDefinition crs) {
//		for (InstanceServiceListener listener : listeners) {
//			listener.crsChanged(crs);
//		}
//	}

	@Override
	public void addListener(InstanceServiceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(InstanceServiceListener listener) {
		listeners.remove(listener);
	}

}
