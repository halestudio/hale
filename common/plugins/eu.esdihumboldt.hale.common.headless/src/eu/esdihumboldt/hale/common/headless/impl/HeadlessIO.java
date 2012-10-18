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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * Utilities for headless execution of I/O configurations and providers.
 * 
 * @author Simon Templer
 */
public abstract class HeadlessIO {

	private static final ALogger log = ALoggerFactory
			.getLogger(ProjectTransformationEnvironment.class);

	/**
	 * Execute a set of I/O configurations. Configurations for which no advisor
	 * is provided are ignored.
	 * 
	 * @param configurations the I/O configurations
	 * @param advisors map of advisors, action ID mapped to responsible advisor
	 * @throws IOException if an error occurs executing a configuration
	 */
	public static void executeConfigurations(final List<IOConfiguration> configurations,
			Map<String, IOAdvisor<?>> advisors) throws IOException {
		// TODO sort by dependencies?
		for (IOConfiguration conf : configurations) {
			executeConfiguration(conf, advisors);
		}
	}

	/**
	 * Execute a single I/O configuration. If no matching advisor is given for
	 * the configuration, it is ignored.
	 * 
	 * @param conf the I/O configuration
	 * @param advisors map of advisors, action ID mapped to responsible advisor
	 * @throws IOException if an error occurs executing a configuration
	 */
	public static void executeConfiguration(IOConfiguration conf, Map<String, IOAdvisor<?>> advisors)
			throws IOException {
		// get advisor ...
		final String actionId = conf.getActionId();
		IOAdvisor<?> advisor = advisors.get(actionId);
		if (advisor == null) {
			log.info(MessageFormat.format(
					"No advisor for action {0} given, I/O configuration is ignored.", actionId));
			// ignore this configuration
			return;
		}

		// ... and provider
		IOProvider provider = null;
		IOProviderDescriptor descriptor = IOProviderExtension.getInstance().getFactory(
				conf.getProviderId());
		if (descriptor != null) {
			try {
				provider = descriptor.createExtensionObject();
			} catch (Exception e) {
				throw new IOException(
						MessageFormat.format(
								"Could not execute I/O configuration, provider with ID {0} could not be created.",
								conf.getProviderId()), e);
			}

			// configure settings
			provider.loadConfiguration(conf.getProviderConfiguration());
			// execute provider
			executeProvider(provider, advisor, null); // XXX progress?!!
		}
		else {
			throw new IOException(MessageFormat.format(
					"Could not execute I/O configuration, provider with ID {0} not found.",
					conf.getProviderId()));
		}
	}

	/**
	 * Execute the given I/O provider with the given I/O advisor.
	 * 
	 * @param provider the I/O provider
	 * @param advisor the I/O advisor
	 * @param progress the progress indicator
	 * @throws IOException if executing the provider fails
	 */
	@SuppressWarnings("unchecked")
	public static void executeProvider(final IOProvider provider,
			@SuppressWarnings("rawtypes") final IOAdvisor advisor, ProgressIndicator progress)
			throws IOException {
		IOReporter reporter = provider.createReporter();
		ATransaction trans = log.begin(reporter.getTaskName());
		try {
			// use advisor to configure provider
			advisor.prepareProvider(provider);
			advisor.updateConfiguration(provider);

			// execute
			IOReport report = provider.execute(progress);

			// TODO do anything with the report?

			// handle results
			if (report.isSuccess()) {
				advisor.handleResults(provider);
			}
			else {
				throw new IOException("Executing I/O provider not successful: "
						+ report.getSummary());
			}
		} catch (Exception e) {
			throw new IOException("Error executing an I/O provider.", e);
		} finally {
			trans.end();
		}
	}

}
