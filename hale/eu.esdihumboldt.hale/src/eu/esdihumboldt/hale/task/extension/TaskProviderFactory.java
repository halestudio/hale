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
package eu.esdihumboldt.hale.task.extension;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import eu.esdihumboldt.hale.task.TaskProvider;

/**
 * Descriptor for {@link TaskProvider}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskProviderFactory {
	
	private static final Logger log = Logger.getLogger(TaskProviderFactory.class);
	
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
		return conf.getAttribute("name");
	}
	
	/**
	 * Get the task provider
	 * 
	 * @return the task provider or <code>null</code> if the
	 *   creation failed
	 */
	public TaskProvider getTaskProvider() {
		try {
			return (TaskProvider) conf.createExecutableExtension("class");
		} catch (CoreException e) {
			log.error("Error creating the function wizard factory", e);
		}
		
		return null;
	}

	/**
	 * Get the task provider ID
	 * 
	 * @return the ID
	 */
	public String getId() {
		return conf.getAttribute("id");
	}

}
