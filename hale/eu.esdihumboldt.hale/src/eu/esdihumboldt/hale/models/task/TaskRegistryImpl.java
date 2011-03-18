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

package eu.esdihumboldt.hale.models.task;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.task.ServiceProvider;
import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.TaskRegistry;
import eu.esdihumboldt.hale.task.TaskType;

/**
 * Task registry implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskRegistryImpl implements TaskRegistry {
	
	private final Map<String, TaskType> types = new HashMap<String, TaskType>();

	/**
	 * @see TaskRegistry#getType(String)
	 */
	@Override
	public TaskType getType(String typeName) {
		return types.get(typeName);
	}

	/**
	 * @see TaskRegistry#registerType(TaskType)
	 */
	@Override
	public void registerType(TaskType type) throws IllegalStateException {
		if (types.containsKey(type.getName())) {
			throw new IllegalStateException("Duplicate task type: " + type.getName()); //$NON-NLS-1$
		}
		else {
			types.put(type.getName(), type);
		}
	}

	/**
	 * @see TaskRegistry#createTask(ServiceProvider, String, Definition[])
	 */
	@Override
	public Task createTask(ServiceProvider serviceProvider, String typeName,
			Definition... definitions) {
		TaskType type = getType(typeName);
		if (type != null) {
			return type.getTaskFactory().createTask(serviceProvider, definitions);
		}
		else {
			return null;
		}
	}

}
