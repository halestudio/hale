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

package eu.esdihumboldt.hale.common.headless;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
import eu.esdihumboldt.hale.common.core.io.CachingImportProvider;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorFactory;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;

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
	 * @param reportHandler the report handler, may be <code>null</code>
	 * @param serviceProvider the service provider
	 * @throws IOException if an error occurs executing a configuration
	 */
	public static void executeConfigurations(final List<IOConfiguration> configurations,
			Map<String, IOAdvisor<?>> advisors, ReportHandler reportHandler,
			ServiceProvider serviceProvider) throws IOException {
		// TODO sort by dependencies?
		for (IOConfiguration conf : configurations) {
			executeConfiguration(conf, advisors, reportHandler, serviceProvider);
		}
	}

	/**
	 * Execute a single I/O configuration. If no matching advisor is given for
	 * the configuration, first the extension point is queried for an advisor,
	 * if not found it is ignored.
	 * 
	 * @param conf the I/O configuration
	 * @param advisors map of advisors, action ID mapped to responsible advisor
	 * @param reportHandler the report handler, may be <code>null</code>
	 * @param serviceProvider the service provider
	 * @throws IOException if an error occurs executing a configuration
	 */
	public static void executeConfiguration(IOConfiguration conf,
			Map<String, IOAdvisor<?>> advisors, ReportHandler reportHandler,
			ServiceProvider serviceProvider) throws IOException {
		// get advisor ...
		final String actionId = conf.getActionId();
		IOAdvisor<?> advisor = advisors.get(actionId);
		if (advisor == null) {
			// try to find registered advisor for action (e.g. lookup)
			List<IOAdvisorFactory> regAdvisors = IOAdvisorExtension.getInstance()
					.getFactories(new FactoryFilter<IOAdvisor<?>, IOAdvisorFactory>() {

						@Override
						public boolean acceptFactory(IOAdvisorFactory factory) {
							return factory.getActionID().equals(actionId);
						}

						@Override
						public boolean acceptCollection(
								ExtensionObjectFactoryCollection<IOAdvisor<?>, IOAdvisorFactory> collection) {
							return true;
						}
					});
			if (regAdvisors != null && !regAdvisors.isEmpty()) {
				try {
					advisor = regAdvisors.get(0).createAdvisor(serviceProvider);
					log.info(MessageFormat.format(
							"No advisor for action {0} given, using advisor registered through extension point.",
							actionId));
				} catch (Exception e) {
					log.error(MessageFormat
							.format("Failed to load registered advisor for action {0}.", actionId));
					if (advisor != null) {
						// shouldn't happen, but seems to happen anyway
						log.error("Advisor is set in spite of error", e);
					}
				}
			}
		}
		if (advisor == null) {
			log.info(MessageFormat.format(
					"No advisor for action {0} given, I/O configuration is ignored.", actionId));
			// ignore this configuration
			return;
		}

		// ... and provider
		IOProvider provider = loadProvider(conf);
		if (serviceProvider != null) {
			// set service provider, just in case the advisor does not do it
			provider.setServiceProvider(serviceProvider);
		}
		if (provider != null) {
			// execute provider
			executeProvider(provider, advisor, null, reportHandler);
			// XXX progress?!!
		}
		else {
			throw new IOException(MessageFormat.format(
					"Could not execute I/O configuration, provider with ID {0} not found.",
					conf.getProviderId()));
		}
	}

	/**
	 * Load and configure the I/O provider specified by the given I/O
	 * configuration.
	 * 
	 * @param conf the I/O configuration
	 * @return the provider or <code>null</code> if it was not found or could
	 *         not be created
	 */
	public static IOProvider loadProvider(IOConfiguration conf) {
		IOProvider provider = null;
		IOProviderDescriptor descriptor = IOProviderExtension.getInstance()
				.getFactory(conf.getProviderId());
		if (descriptor != null) {
			try {
				provider = descriptor.createExtensionObject();

				// configure settings
				provider.loadConfiguration(conf.getProviderConfiguration());
				if (provider instanceof CachingImportProvider) {
					((CachingImportProvider) provider).setCache(conf.getCache());
				}
			} catch (Exception e) {
				log.error("Could not instantiate I/O provider.", e);
			}
		}

		return provider;
	}

	/**
	 * Execute the given I/O provider with the given I/O advisor.
	 * 
	 * @param provider the I/O provider
	 * @param advisor the I/O advisor
	 * @param progress the progress indicator, may be <code>null</code>
	 * @param reportHandler the report handler, may be <code>null</code>
	 * @throws IOException if executing the provider fails
	 */
	@SuppressWarnings("unchecked")
	public static void executeProvider(final IOProvider provider,
			@SuppressWarnings("rawtypes") final IOAdvisor advisor, ProgressIndicator progress,
			ReportHandler reportHandler) throws IOException {
		IOReporter reporter = provider.createReporter();
		ATransaction trans = log.begin(reporter.getTaskName());
		try {
			// use advisor to configure provider
			advisor.prepareProvider(provider);
			advisor.updateConfiguration(provider);

			// execute
			IOReport report = provider.execute(progress);
			if (reportHandler != null) {
				reportHandler.publishReport(report);
			}

			// handle results
			if (report.isSuccess()) {
				advisor.handleResults(provider);
			}
			else {
				throw new IOException(
						"Executing I/O provider not successful: " + report.getSummary());
			}
		} catch (Exception e) {
			throw new IOException("Error executing an I/O provider.", e);
		} finally {
			trans.end();
		}
	}

}
