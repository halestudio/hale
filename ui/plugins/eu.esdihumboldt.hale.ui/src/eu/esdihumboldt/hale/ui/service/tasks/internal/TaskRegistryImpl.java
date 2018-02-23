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

package eu.esdihumboldt.hale.ui.service.tasks.internal;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.tasks.TaskRegistry;
import eu.esdihumboldt.hale.common.tasks.TaskType;

/**
 * Task registry implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskRegistryImpl implements TaskRegistry {

	private final Map<String, TaskType<?>> types = new HashMap<String, TaskType<?>>();

	/**
	 * @see TaskRegistry#getType(String)
	 */
	@Override
	public TaskType<?> getType(String typeName) {
		return types.get(typeName);
	}

	/**
	 * @see TaskRegistry#registerType(TaskType)
	 */
	@Override
	public void registerType(TaskType<?> type) throws IllegalStateException {
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
//	@Override
//	public Task createTask(ServiceProvider serviceProvider, String typeName,
//			Definition... definitions) {
//		TaskType type = getType(typeName);
//		if (type != null) {
//			return type.getTaskFactory().createTask(serviceProvider, definitions);
//		}
//		else {
//			return null;
//		}
//	}

}
