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
