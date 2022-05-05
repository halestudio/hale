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

package eu.esdihumboldt.hale.io.json;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.io.json.writer.InstanceToJson;

/**
 * Writes instances as JSON.
 * 
 * @author Sebastian Reinhardt
 */
public class JsonInstanceWriter extends AbstractInstanceWriter {

	InstanceToJson instanceToJson;
	boolean useGeoJsonFeatures = false;

	/**
	 * By default do not use geoJson features for the output.
	 */
	public JsonInstanceWriter() {
		this(false);
	}

	/**
	 *
	 * Note: The GeoJson output follows the RFC SPEC but extends it similar to
	 * the draft version 6 by attributes for namespace prefix definitions and
	 * specific feature type: http://wiki.geojson.org/GeoJSON_draft_version_6
	 *
	 * @param useGeoJsonFeatures if the output should be valid GeoJson output
	 */
	public JsonInstanceWriter(boolean useGeoJsonFeatures) {
		this.useGeoJsonFeatures = useGeoJsonFeatures;
		instanceToJson = new InstanceToJson(useGeoJsonFeatures);
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generating JSON", ProgressIndicator.UNKNOWN);

		try {
			writeInstanceCollectionToJson(getInstances(), reporter);
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Error generating JSON file", e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;

	}

	/**
	 * Method to write collection of instances to output
	 * 
	 * @param instanceCollection the collection of instances
	 * @param reporter the reporter
	 * @throws Exception if writer instance fails
	 */
	public void writeInstanceCollectionToJson(InstanceCollection instanceCollection,
			IOReporter reporter) throws Exception {
		LocatableOutputSupplier<? extends OutputStream> out = getTarget();

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(out.getOutput(), Charset.forName("UTF-8")))) {

			InstanceToJson.<Void> withJsonGenerator(writer, true, json -> {
				try {
					instanceToJson.writeCollection(json, instanceCollection, reporter);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
				return null;
			});
		}
	}

	@Override
	public boolean isPassthrough() {
		return true;
	}

	@Override
	protected String getDefaultTypeName() {
		return "JSON";
	}

}
