/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.esdihumboldt.hale.models.instance.InstanceServiceImpl;
import eu.esdihumboldt.hale.task.Task;

/**
 * The {@link TaskProviderFactory} manages all {@link TaskProvider}s available
 * in a given HALE application.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskProviderFactory {
	
	private static Logger _log = Logger.getLogger(InstanceProviderFactory.class);
	
	private static TaskProviderFactory instance = new TaskProviderFactory();
	
	private static Map<String, TaskProvider> providers;
	
	private TaskProviderFactory() {
		TaskProviderFactory.providers = new HashMap<String, TaskProvider>();
		
		// initialize InstanceProviders. TODO: use external config file.
		List<String> tpClassNames = new ArrayList<String>();
		tpClassNames.add("eu.esdihumboldt.hale.models.task.SchemaLoadingTaskProvider");
		for (String tpClassName : tpClassNames) {
			try {
				Class<?> task_provider_class = Class.forName(tpClassName);
				TaskProvider<?> task_provider = (TaskProvider<?>) task_provider_class
						.newInstance();
				TaskProviderFactory.providers.put(task_provider
						.getSupportedInputType(), task_provider);
			} catch (Exception ex) {
				_log.error("Unable to instantiate a TaskProvider object " +
						"belonging to the requested input object type."	+ ex);
			}
		}
		_log.info("Completed TaskProviderFactory setup. "
				+ providers.keySet().size() + " TaskProvider(s) loaded.");
	}
	
	/**
	 * @return the singleton instance of the {@link InstanceServiceImpl}.
	 */
	public static TaskProviderFactory getInstance() {
		return TaskProviderFactory.instance;
	}
	
	public Set<Task> getTasks(Object input) {
		TaskProvider tp = TaskProviderFactory.getInstance().providers
				.get(TaskProviderFactory.buildKeyString(input));
		if (tp == null) {
			// to prevent NullPointerException (ST)
			return new HashSet<Task>();
		}
		return tp.createTasks(input);
	}
	
	public static String buildKeyString(Object input){
		return "java.util.List<org.opengis.feature.type.FeatureType>"; // FIXME
	}

}
