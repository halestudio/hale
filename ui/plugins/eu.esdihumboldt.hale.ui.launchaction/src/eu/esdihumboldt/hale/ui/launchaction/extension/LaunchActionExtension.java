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

package eu.esdihumboldt.hale.ui.launchaction.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;
import eu.esdihumboldt.hale.ui.launchaction.LaunchAction;

/**
 * Launch action extension.
 * 
 * @author Simon Templer
 */
public class LaunchActionExtension extends AbstractExtension<LaunchAction, LaunchActionFactory> {

	/**
	 * Launch action factory based on configuration element.
	 */
	public class ConfigFactory extends AbstractConfigurationFactory<LaunchAction> implements
			LaunchActionFactory {

		/**
		 * 
		 * @param conf the configuration element defining the factory
		 */
		protected ConfigFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(LaunchAction action) {
			// XXX will there be any calls to this?
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

	/**
	 * Identifier of the extension point.
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.ui.launchaction";

	private static LaunchActionExtension instance;

	/**
	 * Get the extension singleton instance.
	 * 
	 * @return the extension instance
	 */
	public static LaunchActionExtension getInstance() {
		synchronized (LaunchActionExtension.class) {
			if (instance == null)
				instance = new LaunchActionExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	public LaunchActionExtension() {
		super(EXTENSION_ID);
	}

	@Override
	protected LaunchActionFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ConfigFactory(conf);
	}

}
