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

package eu.esdihumboldt.hale.io.wfs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.SubtaskProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractGeoInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.tools.InstanceCollectionPartitioner;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;

/**
 * WFS writer that publishes partitioned data sets
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class PartitioningWFSWriter extends AbstractGeoInstanceWriter
		implements WFSWriter, WFSConstants {

	/**
	 * Name of the parameter defining the instance threshold.
	 */
	public static final String PARAM_INSTANCES_THRESHOLD = "instancesPerRequest";

	/**
	 * Default value for the instance threshold.
	 */
	public static final int DEFAULT_INSTANCES_THRESHOLD = 15000;

	@Override
	protected IOReport execute(final ProgressIndicator progress, final IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Upload to WFS-T", IProgressMonitor.UNKNOWN);
		try {
			progress.setCurrentTask("Partitioning data");

			// create the partitioner
			InstanceCollectionPartitioner partitioner = StreamGmlWriter.getPartitioner(this,
					reporter);

			// partition the graph
			int threshold = getParameter(PARAM_INSTANCES_THRESHOLD).as(Integer.class,
					DEFAULT_INSTANCES_THRESHOLD);
			try (ResourceIterator<InstanceCollection> parts = partitioner.partition(getInstances(),
					threshold, reporter)) {

				if (partitioner.requiresImmediateConsumption()) {

					// handle all parts right here, one after another

					int partCount = 0;
					boolean failed = false;
					if (parts.hasNext()) {
						while (parts.hasNext() && !progress.isCanceled()) {
							partCount++;

							SubtaskProgressIndicator partitionProgress = new SubtaskProgressIndicator(
									progress);
							partitionProgress.begin("Assembling part " + partCount,
									ProgressIndicator.UNKNOWN);
							InstanceCollection part = parts.next();
							partitionProgress.end();

							progress.setCurrentTask("Upload part " + partCount + ((part.hasSize())
									? (" (" + part.size() + " instances)") : ("")));

							IOReport report = uploadInstances(part, reporter,
									new SubtaskProgressIndicator(progress));
							if (!report.isSuccess()) {
								failed = true;
								reporter.error("Upload of part {0} - {1}", partCount,
										report.getSummary());
							}
							else {
								reporter.info("Upload of part {0} - {1}", partCount,
										report.getSummary());
							}
						}

						reporter.setSuccess(!failed && reporter.getErrors().isEmpty());
						if (!reporter.isSuccess()) {
							reporter.setSummary(
									"Errors during upload to WFS-T, please see the report.");
						}
						else {
							reporter.setSummary("Successfully uploaded data via WFS-T");
						}
					}
					else {
						reporter.setSuccess(false);
						reporter.setSummary("Partitioning yielded no instances to upload");
					}

				}
				else {
					// can start requests with separate thread (potentially
					// threads, but tests with WFSes show that this usually is
					// too much to handle for the service)

					int partCount = 0;
					final AtomicBoolean failed = new AtomicBoolean();
					if (parts.hasNext()) {
						ExecutorService requestThread = Executors.newSingleThreadExecutor();

						while (parts.hasNext() && !progress.isCanceled()) {
							partCount++;

							SubtaskProgressIndicator partitionProgress = new SubtaskProgressIndicator(
									progress); // only used for first
												// partitioning
							if (partCount == 1)
								partitionProgress.begin("Assembling part " + partCount,
										ProgressIndicator.UNKNOWN);
							final InstanceCollection part = parts.next(); // not
																			// thread
																			// safe
							if (partCount == 1)
								partitionProgress.end();

							progress.setCurrentTask("Upload part " + partCount + ((part.hasSize())
									? (" (" + part.size() + " instances)") : ("")));

							final int currentPart = partCount;
							requestThread.submit(new Runnable() {

								@Override
								public void run() {
									try {
										IOReport report = uploadInstances(part, reporter,
												new SubtaskProgressIndicator(progress));
										if (!report.isSuccess()) {
											failed.set(true);
											reporter.error(new IOMessageImpl("Upload of part "
													+ currentPart + " - " + report.getSummary(),
													null));
										}
										else {
											reporter.info(new IOMessageImpl("Upload of part "
													+ currentPart + " - " + report.getSummary(),
													null));
										}
									} catch (Exception e) {
										failed.set(true);
										reporter.error(new IOMessageImpl(
												"Upload of part " + currentPart + " failed", e));
									}
								}
							});

						}

						// wait for requests completion
						requestThread.shutdown();
						if (!requestThread.awaitTermination(24, TimeUnit.HOURS)) {
							reporter.error(new IOMessageImpl(
									"Timeout reached waiting for completion of WFS requests",
									null));
						}

						reporter.setSuccess(!failed.get() && reporter.getErrors().isEmpty());
						if (!reporter.isSuccess()) {
							reporter.setSummary(
									"Errors during upload to WFS-T, please see the report.");
						}
						else {
							reporter.setSummary("Successfully uploaded data via WFS-T");
						}
					}
					else {
						reporter.setSuccess(false);
						reporter.setSummary("Partitioning yielded no instances to upload");
					}

				}

			}
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Error during attempt to upload to WFS-T", e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}

		return reporter;
	}

	/**
	 * Upload instances via the WFS-T interface.
	 * 
	 * @param instances the instances to upload
	 * @param reporter the reporter
	 * @param progress the progress indicator
	 * @return the
	 * @throws IOProviderConfigurationException - if the I/O provider was not
	 *             configured properly
	 * @throws IOException - if an I/O operation fails
	 */
	protected IOReport uploadInstances(final InstanceCollection instances,
			final IOReporter reporter, final ProgressIndicator progress)
					throws IOProviderConfigurationException, IOException {
		SimpleWFSWriter writer = new SimpleWFSWriter() {

			@Override
			public IOReporter createReporter() {
				// reuse parent reporter
				return reporter;
			}

		};

		// copy configuration
		Map<String, Value> config = new HashMap<>();
		this.storeConfiguration(config);
		writer.loadConfiguration(config);

		// apply configuration not based on parameters
		writer.setServiceProvider(getServiceProvider());
		writer.setTargetSchema(getTargetSchema());
		writer.setTarget(getTarget());
		writer.setInstances(instances);

		return writer.execute(progress);
	}

	@Override
	public boolean isPassthrough() {
		return !StreamGmlWriter.getPartitioner(this, SimpleLog.NO_LOG).usesReferences();
	}

	@Override
	public boolean isCancelable() {
		return true;
	}

	@Override
	public void setWFSVersion(WFSVersion version) {
		setParameter(PARAM_WFS_VERSION, Value.of(version.versionString));
	}

	@Override
	public WFSVersion getWFSVersion() {
		String versionString = getParameter(PARAM_WFS_VERSION).as(String.class);
		if (versionString == null) {
			return null;
		}
		else
			return WFSVersion.fromString(versionString, null);
	}

	@Override
	protected String getDefaultTypeName() {
		return "WFS-T";
	}

}
