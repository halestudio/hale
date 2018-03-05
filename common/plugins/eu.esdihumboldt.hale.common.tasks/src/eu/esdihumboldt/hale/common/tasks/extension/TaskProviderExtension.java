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
package eu.esdihumboldt.hale.common.tasks.extension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.hale.common.tasks.TaskProvider;

/**
 * Task provider extension utilities
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskProviderExtension extends AbstractExtension<TaskProvider, TaskProviderFactory> {

	private static class TaskProviderFactoryImpl extends AbstractConfigurationFactory<TaskProvider>
			implements TaskProviderFactory {

		/**
		 * Create the TaskProviderFactory for the given configuration element
		 * 
		 * @param conf the configuration element
		 */
		public TaskProviderFactoryImpl(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(TaskProvider instance) {
			// do nothing
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.common.tasks"; //$NON-NLS-1$

	/**
	 * Default constructor
	 */
	public TaskProviderExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected TaskProviderFactory createFactory(IConfigurationElement conf) throws Exception {
		return new TaskProviderFactoryImpl(conf);
	}

	/**
	 * Get the defined task provider factories
	 * 
	 * @return the task provider factories
	 */
	public static List<TaskProviderFactory> getTaskProviderFactories() {
		IConfigurationElement[] confArray = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ID);

		List<TaskProviderFactory> result = new ArrayList<TaskProviderFactory>();

		for (IConfigurationElement conf : confArray) {
			result.add(new TaskProviderFactoryImpl(conf));
		}

		return result;
	}

}
