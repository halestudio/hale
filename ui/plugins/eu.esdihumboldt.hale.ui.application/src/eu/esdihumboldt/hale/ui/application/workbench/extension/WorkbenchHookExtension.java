/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.application.workbench.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.hale.ui.application.workbench.WorkbenchHook;

/**
 * {@link WorkbenchHook} extension.
 * 
 * @author Simon Templer
 */
public class WorkbenchHookExtension extends AbstractExtension<WorkbenchHook, WorkbenchHookFactory> {

	/**
	 * {@link WorkbenchHook} factory based on a configuration element.
	 */
	private static class ConfigurationFactory extends AbstractConfigurationFactory<WorkbenchHook>
			implements WorkbenchHookFactory {

		/**
		 * @param conf the configuration element
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(WorkbenchHook arg0) {
			// nothing to do
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

	}

	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.ui.workbench";

	/**
	 * Default constructor.
	 */
	public WorkbenchHookExtension() {
		super(EXTENSION_ID);
	}

	@Override
	protected WorkbenchHookFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ConfigurationFactory(conf);
	}

}
