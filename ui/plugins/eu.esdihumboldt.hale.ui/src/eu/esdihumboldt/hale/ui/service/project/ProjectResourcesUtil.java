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

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorFactory;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.util.ThreadProgressMonitor;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

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
	 */
	public static void executeConfiguration(IOConfiguration conf) {
		executeConfiguration(conf, null);
	}

	/**
	 * Execute a single I/O configuration with a custom advisor.
	 * 
	 * @param conf the I/O configuration
	 * @param customAdvisor the custom advisor to use or <code>null</code>
	 */
	public static void executeConfiguration(IOConfiguration conf, IOAdvisor<?> customAdvisor) {
		// get provider ...
		IOProvider provider = null;
		IOProviderDescriptor descriptor = IOProviderExtension.getInstance().getFactory(
				conf.getProviderId());
		if (descriptor != null) {
			try {
				provider = descriptor.createExtensionObject();
			} catch (Exception e) {
				log.error(
						MessageFormat
								.format("Could not execute I/O configuration, provider with ID {0} could not be created.",
										conf.getProviderId()), e);
				return;
			}

			// ... and advisor
			final String actionId = conf.getActionId();
			IOAdvisor<?> advisor = customAdvisor;
			if (advisor == null) {
				List<IOAdvisorFactory> advisors = IOAdvisorExtension.getInstance().getFactories(
						new FactoryFilter<IOAdvisor<?>, IOAdvisorFactory>() {

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
						log.error(
								MessageFormat
										.format("Could not execute I/O configuration, advisor with ID {0} could not be created.",
												advisors.get(0).getIdentifier()), e);
						return;
					}
				}
			}

			if (advisor != null) {
				// configure settings
				provider.loadConfiguration(conf.getProviderConfiguration());
				// execute provider
				executeProvider(provider, advisor);
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
	 */
	public static void executeProvider(final IOProvider provider,
			@SuppressWarnings("rawtypes") final IOAdvisor advisor) {
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@SuppressWarnings("unchecked")
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				IOReporter reporter = provider.createReporter();
				ATransaction trans = log.begin(reporter.getTaskName());
				try {
					// use advisor to configure provider
					advisor.prepareProvider(provider);
					advisor.updateConfiguration(provider);

					// execute
					IOReport report = provider.execute(new ProgressMonitorIndicator(monitor));

					// publish report
					ReportService rs = (ReportService) PlatformUI.getWorkbench().getService(
							ReportService.class);
					rs.addReport(report);

					// handle results
					if (report.isSuccess()) {
						advisor.handleResults(provider);
					}
				} catch (Exception e) {
					log.error("Error executing an I/O provider.", e);
				} finally {
					trans.end();
				}
			}
		};
		try {
			ThreadProgressMonitor.runWithProgressDialog(op, provider.isCancelable());
		} catch (Exception e) {
			log.error("Error executing an I/O provider.", e);
		}
	}

}
