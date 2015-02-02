/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.io.target.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.io.ExportTarget;

/**
 * Extension for export targets.
 * 
 * @author Simon Templer
 */
public class ExportTargetExtension extends AbstractExtension<ExportTarget<?>, ExportTargetFactory> {

	/**
	 * Configuration factory for a registered target.
	 */
	public static class ConfigurationFactory extends AbstractConfigurationFactory<ExportTarget<?>>
			implements ExportTargetFactory {

		/**
		 * @param conf the configuration element
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(ExportTarget<?> instance) {
			instance.dispose();
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
	public static final String ID = "eu.esdihumboldt.hale.ui.io.target";

	private static ExportTargetExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the instance
	 */
	public static ExportTargetExtension getInstance() {
		if (instance == null) {
			instance = new ExportTargetExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private ExportTargetExtension() {
		super(ID);
	}

	@Override
	protected ExportTargetFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("target")) {
			return new ConfigurationFactory(conf);
		}
		else {
			return null;
		}
	}

	/**
	 * Get the target factory appropriate for the given provider.
	 * 
	 * @param providerType the provider type
	 * @param providerId the provider identifier
	 * @return the factory for the appropriate export target
	 */
	public ExportTargetFactory forProvider(Class<? extends ExportProvider> providerType,
			String providerId) {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ID);
		for (IConfigurationElement element : elements) {
			if (element.getName().equals("providerTarget")
					&& providerId.equals(element.getAttribute("provider"))) {
				String targetId = element.getAttribute("target");
				ExportTargetFactory factory = getFactory(targetId);
				if (factory != null) {
					return factory;
				}
			}
		}

		// fall-back factory
		if (InstanceWriter.class.isAssignableFrom(providerType)) {
			// default for instance writer: file with validation
			return getFactory("instanceFile");
		}
		else {
			// default: file
			return getFactory("file");
		}
	}

}
