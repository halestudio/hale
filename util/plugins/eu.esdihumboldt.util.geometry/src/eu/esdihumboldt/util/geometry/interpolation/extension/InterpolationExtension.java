/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.geometry.interpolation.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.util.geometry.interpolation.InterpolationAlgorithm;

/**
 * Interpolation algorithm extension point.
 * 
 * @author Simon Templer
 */
public class InterpolationExtension
		extends AbstractExtension<InterpolationAlgorithm, InterpolationAlgorithmFactory> {

	private volatile static InterpolationExtension instance;

	/**
	 * @return the extension instance
	 */
	public static InterpolationExtension getInstance() {
		if (instance == null) {
			instance = new InterpolationExtension();
		}
		return instance;
	}

	/**
	 * Default factory based on extension configuration.
	 */
	public class ConfigurationFactory extends AbstractConfigurationFactory<InterpolationAlgorithm>
			implements InterpolationAlgorithmFactory {

		/**
		 * Create a new factory based on the given configuration element
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(InterpolationAlgorithm instance) {
			// nothing to do
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

	}

	private static final String ID = "eu.esdihumboldt.util.geometry.interpolation";

	/**
	 * Default constructor.
	 */
	public InterpolationExtension() {
		super(ID);
	}

	@Override
	protected InterpolationAlgorithmFactory createFactory(IConfigurationElement conf)
			throws Exception {
		return new ConfigurationFactory(conf);
	}

}
