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

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.util.Map;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceListener;

/**
 * Abstract base implementation for project services
 * 
 * @author Simon Templer
 */
public abstract class AbstractProjectService implements ProjectService {

	private final TypeSafeListenerList<ProjectServiceListener> listeners = new TypeSafeListenerList<ProjectServiceListener>();

	/**
	 * @see ProjectService#addListener(ProjectServiceListener)
	 */
	@Override
	public void addListener(ProjectServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see ProjectService#removeListener(ProjectServiceListener)
	 */
	@Override
	public void removeListener(ProjectServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Call before a project is saved.
	 * 
	 * @param projectFiles the map of additional project files, listeners may
	 *            add additional files to the map
	 */
	protected void notifyBeforeSave(Map<String, ProjectFile> projectFiles) {
		for (ProjectServiceListener listener : listeners) {
			listener.beforeSave(this, projectFiles);
		}
	}

	/**
	 * Call after a project was loaded.
	 * 
	 * @param projectFiles the additional project files that were loaded,
	 *            listeners may use them to update their state
	 */
	protected void notifyAfterLoad(Map<String, ProjectFile> projectFiles) {
		for (ProjectServiceListener listener : listeners) {
			listener.afterLoad(this, projectFiles);
		}
	}

	/**
	 * Call when the project is cleaned.
	 */
	protected void notifyClean() {
		for (ProjectServiceListener listener : listeners) {
			listener.onClean();
		}
	}

}
