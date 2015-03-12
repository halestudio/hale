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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.SubtaskProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.graph.reference.ReferenceGraph;
import eu.esdihumboldt.hale.common.instance.graph.reference.impl.XMLInspector;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractGeoInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;

/**
 * WFS writer that publishes partitioned data sets
 * 
 * @author Simon Templer
 */
public class PartitioningWFSWriter extends AbstractGeoInstanceWriter implements WFSWriter,
		WFSConstants {

	/**
	 * Name of the parameter defining the instance threshold.
	 */
	public static final String PARAM_INSTANCES_THRESHOLD = "instancesPerRequest";

	/**
	 * Default value for the instance threshold.
	 */
	public static final int DEFAULT_INSTANCES_THRESHOLD = 15000;

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Upload to WFS-T", IProgressMonitor.UNKNOWN);
		try {
			progress.setCurrentTask("Partitioning data");

			// create a reference graph
			ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(),
					getInstances());

			// partition the graph
			int threshold = getParameter(PARAM_INSTANCES_THRESHOLD).as(Integer.class,
					DEFAULT_INSTANCES_THRESHOLD);
			Iterator<InstanceCollection> parts = rg.partition(threshold);

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

					progress.setCurrentTask("Upload part " + partCount
							+ ((part.hasSize()) ? (" (" + part.size() + " instances)") : ("")));

					// XXX do this in multiple threads? does that make sense for
					// talking to a WFS-T?

					IOReport report = uploadInstances(part, reporter, new SubtaskProgressIndicator(
							progress));
					if (!report.isSuccess()) {
						failed = true;
						reporter.error(new IOMessageImpl("Upload of part " + partCount + " - "
								+ report.getSummary(), null));
					}
					else {
						reporter.info(new IOMessageImpl("Upload of part " + partCount + " - "
								+ report.getSummary(), null));
					}
				}

				reporter.setSuccess(!failed && reporter.getErrors().isEmpty());
				if (!reporter.isSuccess()) {
					reporter.setSummary("Errors during upload to WFS-T, please see the report.");
				}
				else {
					reporter.setSummary("Successfully uploaded data via WFS-T");
				}
			}
			else {
				reporter.setSuccess(false);
				reporter.setSummary("Partitioning yielded no instances to upload");
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
		return false;
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
