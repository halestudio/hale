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

package eu.esdihumboldt.hale.common.headless.transform.extension;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import eu.esdihumboldt.hale.common.headless.transform.TransformationSink;

/**
 * Extension point for transformation sinks.
 * 
 * @author Simon Templer
 */
public class TransformationSinkExtension extends
		AbstractExtension<TransformationSink, TransformationSinkDescriptor> {

	private static class ConfigurationDescriptor extends
			AbstractConfigurationFactory<TransformationSink> implements
			TransformationSinkDescriptor {

		/**
		 * Creates a new descriptor.
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationDescriptor(IConfigurationElement conf) {
			super(conf, "class");
		}

		@Override
		public void dispose(TransformationSink instance) {
			// nothing to do
		}

		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		@Override
		public int getPriority() {
			int priority;
			try {
				priority = Integer.parseInt(conf.getAttribute("priority"));
			} catch (Exception e) {
				priority = 0;
			}
			return -priority;
		}

		@Override
		public boolean isReiterable() {
			return Boolean.parseBoolean(conf.getAttribute("reiterable"));
			// defaults to false
		}

	}

	/**
	 * The extension point identifier.
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.headless.sink";

	private static final TransformationSinkExtension INSTANCE = new TransformationSinkExtension();

	/**
	 * @return the extension singleton instance
	 */
	public static TransformationSinkExtension getInstance() {
		return INSTANCE;
	}

	/**
	 * Default constructor.
	 */
	protected TransformationSinkExtension() {
		super(EXTENSION_ID);
	}

	@Override
	protected TransformationSinkDescriptor createFactory(IConfigurationElement conf)
			throws Exception {
		if (conf.getName().equals("sink")) {
			return new ConfigurationDescriptor(conf);
		}
		return null;
	}

	/**
	 * Create a new transformation sink.
	 * 
	 * @param reiterable if the transformation sink should be reiterable
	 * @return the transformation sink
	 * @throws Exception if no applicable transformation sink can be created
	 */
	public TransformationSink createSink(final boolean reiterable) throws Exception {
		List<TransformationSinkDescriptor> candidates = getFactories(new FactoryFilter<TransformationSink, TransformationSinkDescriptor>() {

			@Override
			public boolean acceptFactory(TransformationSinkDescriptor factory) {
				return factory.isReiterable() == reiterable;
			}

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<TransformationSink, TransformationSinkDescriptor> collection) {
				return true;
			}
		});
		if (!candidates.isEmpty()) {
			return candidates.get(0).createExtensionObject();
			// TODO try other candidates?
		}
		else {
			throw new Exception("No applicable transformation sink found");
		}
	}

}
