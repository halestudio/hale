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
package eu.esdihumboldt.hale.ui.views.tasks.model.extension;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskProvider;

/**
 * Descriptor for {@link TaskProvider}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskProviderFactory {
	
	private static final ALogger log = ALoggerFactory.getLogger(TaskProviderFactory.class);
	
	private final IConfigurationElement conf;
	
	/**
	 * Constructor
	 * 
	 * @param conf the configuration element describing the
	 *   {@link TaskProvider}
	 */
	public TaskProviderFactory(final IConfigurationElement conf) {
		super();
		
		this.conf = conf;
	}
	
	/**
	 * Get the wizard name
	 * 
	 * @return the wizard name
	 */
	public String getName() {
		return conf.getAttribute("name"); //$NON-NLS-1$
	}
	
	/**
	 * Get the wizard description
	 * 
	 * @return the wizard description or <code>null</code>
	 */
	public String getDescription() {
		return conf.getAttribute("description"); //$NON-NLS-1$
	}
	
	/**
	 * Get if the task provider should be enabled by default
	 * 
	 * @return if the task provider should be enabled by default
	 */
	public boolean isDefaultEnabled() {
		return Boolean.parseBoolean(conf.getAttribute("defaultEnabled")); //$NON-NLS-1$
	}
	
	/**
	 * Get the task provider
	 * 
	 * @return the task provider or <code>null</code> if the
	 *   creation failed
	 */
	public TaskProvider getTaskProvider() {
		try {
			return (TaskProvider) conf.createExecutableExtension("class"); //$NON-NLS-1$
		} catch (CoreException e) {
			log.error("Error creating the task provider factory", e); //$NON-NLS-1$
		}
		
		return null;
	}

	/**
	 * Get the task provider ID
	 * 
	 * @return the ID
	 */
	public String getId() {
		return conf.getAttribute("id"); //$NON-NLS-1$
	}

}
