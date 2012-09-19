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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * Task provider extension utilities
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskProviderExtension {
	
	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.task.TaskProvider"; //$NON-NLS-1$
	
	/**
	 * Get the defined task provider factories
	 * 
	 * @return the task provider factories
	 */
	public static List<TaskProviderFactory> getTaskProviderFactories() {
		IConfigurationElement[] confArray = Platform.getExtensionRegistry().getConfigurationElementsFor(ID);
		
		List<TaskProviderFactory> result = new ArrayList<TaskProviderFactory>();
		
		for (IConfigurationElement conf : confArray) {
			// factory
			result.add(new TaskProviderFactory(conf));
		}
		
		return result;
	}

}
