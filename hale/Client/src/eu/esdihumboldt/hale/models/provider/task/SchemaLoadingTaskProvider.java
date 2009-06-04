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
package eu.esdihumboldt.hale.models.provider.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.provider.TaskProvider;
import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.TaskSource;
import eu.esdihumboldt.hale.task.impl.SimpleTask;

/**
 * This {@link TaskProvider} is used when a schema is newly loaded. It creates 
 * one {@link Task} per {@link FeatureType} in the schema.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @param <T>
 */
public class SchemaLoadingTaskProvider 
	implements TaskProvider, TaskSource {

	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.provider.TaskProvider#createTasks(java.lang.Object)
	 */
	@Override
	public Set<Task> createTasks(Object baseObject) {
		Set<Task> result = new HashSet<Task>();
		if (baseObject instanceof List) {
			List types = (List) baseObject;
			for (Object type : types) {
				if (type instanceof FeatureType) {
					FeatureType ft = (FeatureType) type;
					result.add(new SimpleTask(Task.SeverityLevel.task,
							"FeatureType " + ft.getName().getLocalPart()
									+ " has to be mapped.", this));
				}
			}
		}
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.provider.TaskProvider#getSupportedInputType()
	 */
	public String getSupportedInputType() {
		return "java.util.List<org.opengis.feature.type.FeatureType>";
	}

	/**
	 * @see eu.esdihumboldt.hale.task.TaskSource#getImplementationName()
	 */
	public String getImplementationName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * @see eu.esdihumboldt.hale.task.TaskSource#getTaskCreationReason()
	 */
	public String getTaskCreationReason() {
		return "FeatureType loaded";
	}

}
