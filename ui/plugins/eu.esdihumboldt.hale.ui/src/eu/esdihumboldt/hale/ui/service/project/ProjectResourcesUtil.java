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

package eu.esdihumboldt.hale.ui.service.project;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
import eu.esdihumboldt.hale.common.core.io.CachingImportProvider;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorFactory;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.util.io.ThreadProgressMonitor;

/**
 * Utility methods for loading project resources.
 * 
 * @author Simon Templer
 */
public class ProjectResourcesUtil {

	private static final ALogger log = ALoggerFactory.getLogger(ProjectResourcesUtil.class);

	/**
	 * Execute a single I/O configuration.
	 * 
	 * @param conf the I/O configuration
	 * @param cacheCallback call back that is notified on cache changes for the
	 *            I/O configuration, may be <code>null</code>
	 */
	public static void executeConfiguration(IOConfiguration conf, CacheCallback cacheCallback) {
		executeConfiguration(conf, null, true, cacheCallback);
	}

	/**
	 * Execute a single I/O configuration with a custom advisor.
	 * 
	 * @param conf the I/O configuration
	 * @param customAdvisor the custom advisor to use or <code>null</code>
	 * @param publishReport if the report should be published
	 * @param cacheCallback call back that is notified on cache changes for the
	 *            I/O configuration, may be <code>null</code>
	 */
	public static void executeConfiguration(IOConfiguration conf, IOAdvisor<?> customAdvisor,
			boolean publishReport, CacheCallback cacheCallback) {
		// get provider ...
		IOProvider provider = null;
		IOProviderDescriptor descriptor = IOProviderExtension.getInstance()
				.getFactory(conf.getProviderId());
		if (descriptor != null) {
			try {
				provider = descriptor.createExtensionObject();
			} catch (Exception e) {
				log.error(MessageFormat.format(
						"Could not execute I/O configuration, provider with ID {0} could not be created.",
						conf.getProviderId()), e);
				return;
			}

			// ... and advisor
			final String actionId = conf.getActionId();
			IOAdvisor<?> advisor = customAdvisor;
			if (advisor == null) {
				List<IOAdvisorFactory> advisors = IOAdvisorExtension.getInstance()
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
				if (advisors != null && !advisors.isEmpty()) {
					try {
						advisor = advisors.get(0).createAdvisor(HaleUI.getServiceProvider());
					} catch (Exception e) {
						log.error(MessageFormat.format(
								"Could not execute I/O configuration, advisor with ID {0} could not be created.",
								advisors.get(0).getIdentifier()), e);
						return;
					}
				}
			}

			if (advisor != null) {
				// configure settings
				provider.loadConfiguration(conf.getProviderConfiguration());
				if (provider instanceof CachingImportProvider) {
					((CachingImportProvider) provider).setCache(conf.getCache());
				}
				// execute provider
				executeProvider(provider, advisor, publishReport, cacheCallback);
			}
			else {
				log.error(MessageFormat.format(
						"Could not execute I/O configuration, no advisor for action {0} found.",
						actionId));
			}
		}
		else {
			log.error(MessageFormat.format(
					"Could not execute I/O configuration, provider with ID {0} not found.",
					conf.getProviderId()));
		}
	}

	/**
	 * Execute the given I/O provider with the given I/O advisor.
	 * 
	 * @param provider the I/O provider
	 * @param advisor the I/O advisor
	 * @param cacheCallback call back that is notified on cache changes for the
	 *            I/O provider, may be <code>null</code>
	 */
	public static void executeProvider(final IOProvider provider,
			@SuppressWarnings("rawtypes") final IOAdvisor advisor,
			final CacheCallback cacheCallback) {
		executeProvider(provider, advisor, true, cacheCallback);
	}

	/**
	 * Execute the given I/O provider with the given I/O advisor.
	 * 
	 * @param provider the I/O provider
	 * @param advisor the I/O advisor
	 * @param publishReport if the report should be published
	 * @param cacheCallback call back that is notified on cache changes for the
	 *            I/O provider, may be <code>null</code>
	 * @return the future yielding the report on success
	 */
	public static ListenableFuture<IOReport> executeProvider(final IOProvider provider,
			@SuppressWarnings("rawtypes") final IOAdvisor advisor, final boolean publishReport,
			final CacheCallback cacheCallback) {
		final SettableFuture<IOReport> result = SettableFuture.create();
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@SuppressWarnings("unchecked")
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				if (cacheCallback != null && provider instanceof CachingImportProvider) {
					// enable cache generation
					((CachingImportProvider) provider).setProvideCache();
				}

				IOReporter reporter = provider.createReporter();
				ATransaction trans = log.begin(reporter.getTaskName());
				try {
					// use advisor to configure provider
					advisor.prepareProvider(provider);
					advisor.updateConfiguration(provider);

					// execute
					IOReport report = provider.execute(new ProgressMonitorIndicator(monitor));

					if (publishReport) {
						// publish report
						ReportService rs = PlatformUI.getWorkbench()
								.getService(ReportService.class);
						rs.addReport(report);
					}

					// handle cache update
					if (cacheCallback != null && provider instanceof CachingImportProvider) {
						CachingImportProvider cip = (CachingImportProvider) provider;
						if (cip.isCacheUpdate()) {
							Value cache = cip.getCache();
							cacheCallback.update(cache);
						}
					}

					// handle results
					if (report.isSuccess()) {
						advisor.handleResults(provider);
						result.set(report);
					}
				} catch (Exception e) {
					log.error("Error executing an I/O provider.", e);
					result.setException(e);
				} finally {
					trans.end();
				}
			}
		};
		try {
			ThreadProgressMonitor.runWithProgressDialog(op, provider.isCancelable());
		} catch (Exception e) {
			log.error("Error executing an I/O provider.", e);
			result.setException(e);
		}
		return result;
	}
}
