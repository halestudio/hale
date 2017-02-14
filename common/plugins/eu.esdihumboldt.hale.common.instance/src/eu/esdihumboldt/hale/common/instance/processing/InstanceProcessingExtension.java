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

package eu.esdihumboldt.hale.common.instance.processing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinition;

/**
 * Extension for {@link InstanceProcessor}s
 * 
 * @author Florian Esser
 */
public class InstanceProcessingExtension
		extends AbstractExtension<InstanceProcessor, InstanceProcessorFactory> {

	private static final ALogger log = ALoggerFactory.getLogger(InstanceProcessingExtension.class);

	private final List<InstanceProcessor> processors = new ArrayList<>();

	/**
	 * Factory for {@link InstanceProcessor} based on an
	 * {@link IConfigurationElement}.
	 */
	private static class ConfigurationFactory extends
			AbstractConfigurationFactory<InstanceProcessor>implements InstanceProcessorFactory {

		/**
		 * Create a {@link FilterDefinition} factory based on the given
		 * configuration element.
		 * 
		 * @param conf the configuration element
		 */
		public ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(InstanceProcessor instance) {
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
	public static final String ID = "eu.esdihumboldt.hale.instance.processing";

	/**
	 * Create the extension
	 * 
	 * @param serviceProvider Service provider that will be passed to the
	 *            created {@link InstanceProcessor}s
	 */
	public InstanceProcessingExtension(ServiceProvider serviceProvider) {
		super(ID);

		for (InstanceProcessorFactory factory : getFactories()) {
			try {
				InstanceProcessor p = factory.createExtensionObject();
				p.setServiceProvider(serviceProvider);
				processors.add(p);
			} catch (Exception e) {
				log.error(MessageFormat.format("Unable to create InstanceProcessor \"{0}\"",
						factory.getIdentifier()), e);
			}
		}
	}

	/**
	 * @return the available processors
	 */
	public List<InstanceProcessor> getInstanceProcessors() {
		return Collections.unmodifiableList(processors);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected InstanceProcessorFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ConfigurationFactory(conf);
	}
}
