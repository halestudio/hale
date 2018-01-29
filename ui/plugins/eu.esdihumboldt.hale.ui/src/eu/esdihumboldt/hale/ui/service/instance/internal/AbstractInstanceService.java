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

package eu.esdihumboldt.hale.ui.service.instance.internal;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ProjectVariables;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyServiceListener;

/**
 * Notification handling for {@link InstanceService}s that support
 * {@link InstanceServiceListener}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractInstanceService implements InstanceService {

	private final CopyOnWriteArraySet<InstanceServiceListener> listeners = new CopyOnWriteArraySet<InstanceServiceListener>();

	private final AlignmentService alignmentService;
	private final ProjectService projectService;

	private boolean liveTransform = true; // TODO where to store the
											// configuration? project?

	private volatile boolean isTransforming;

	/**
	 * Create an instance service.
	 * 
	 * @param projectService the project service. The instances will be cleared
	 *            when the project is cleaned.
	 * @param alignmentService the alignment service
	 * @param groovyService the groovy service
	 */
	public AbstractInstanceService(ProjectService projectService, AlignmentService alignmentService,
			GroovyService groovyService) {
		super();

		this.alignmentService = alignmentService;
		this.projectService = projectService;

		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				clearInstances();
			}

			@Override
			public void projectSettingChanged(String name, Value value) {
				if (ProjectVariables.PROJECT_PROPERTY_VARIABLES.equals(name)) {
					// project variables changed
					retransform();
				}
			}

		});

		alignmentService.addListener(new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				clearTransformedInstances();
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				/*
				 * TODO analyze cell if it is a type or property mapping
				 * property mapping: retransform based on related type mappings
				 * type mapping: removed transformed instances based on type
				 * mapping
				 */
				retransform();
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				/*
				 * TODO only retransform with relevant cells (i.e. create a view
				 * on the alignment)
				 */
				retransform();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				/*
				 * TODO only retransform with relevant cells (i.e. create a view
				 * on the alignment)
				 */
				retransform();
			}

			@Override
			public void customFunctionsChanged() {
				retransform();
			}

			@Override
			public void alignmentChanged() {
				retransform();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				/*
				 * TODO only retransform with relevant cells (i.e. create a view
				 * on the alignment)
				 */
				retransform();
			}

		});

		groovyService.addListener(new GroovyServiceListener() {

			@Override
			public void restrictionChanged(boolean restrictionActive) {
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
			liveTransform = enabled; // XXX use a lock for liveTransform?
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
	 * Retransform all instances. Decides if a transformation should be done or
	 * not.
	 */
	protected final void retransform() {
		// Retransform only if no other transformation run is currently
		// executing. Such a nested retransform can be triggered by
		// listener notifications, e.g. if the user lifts the Groovy
		// restrictions via the confirmation dialog during a transformation run
		if (isTransformationEnabled() && !isTransforming) {
			isTransforming = true;
			try {
				doRetransform();
			} finally {
				isTransforming = false;
			}
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
		return HalePlatform.getService(TransformationService.class);
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
	 * 
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
	 * @param type the data set type, <code>null</code> if both sets have
	 *            changed
	 */
	protected void notifyDatasetChanged(DataSet type) {
		for (InstanceServiceListener listener : listeners) {
			if (type == null) {
				listener.datasetChanged(DataSet.SOURCE);
				listener.datasetChanged(DataSet.TRANSFORMED);
			}
			else {
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
			}
			else {
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
