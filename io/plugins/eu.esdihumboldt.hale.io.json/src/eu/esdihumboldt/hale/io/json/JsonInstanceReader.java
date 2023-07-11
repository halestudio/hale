/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json;

import java.io.IOException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.json.internal.JsonInstanceCollection;
import eu.esdihumboldt.hale.io.json.internal.JsonToInstance;

/**
 * Reader for Json/GeoJson data.
 * 
 * @author Simon Templer
 */
public class JsonInstanceReader extends AbstractInstanceReader {

	private static final ALogger log = ALoggerFactory.getLogger(JsonInstanceReader.class);

	private InstanceCollection instances;

	@Override
	public InstanceCollection getInstances() {
		return instances;
	}

	@Override
	public boolean isCancelable() {
		// actual
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Creating " + getDefaultTypeName() + " parser", ProgressIndicator.UNKNOWN);

		try {
			boolean expectGeoJson = true; // currently defaults to true, no
											// major difference in functionality

			// FIXME support configuring type; possibly also type detection
			// XXX for now first type found in schema is used
			TypeDefinition type = getSourceSchema().getMappingRelevantTypes().stream().findFirst()
					.orElse(null);

			JsonToInstance translator = new JsonToInstance(expectGeoJson, type,
					SimpleLog.fromLogger(log));
			instances = new JsonInstanceCollection(translator, getSource(), getCharset());

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error("Error preparing reading {0}", getDefaultTypeName(), e);
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "JSON";
	}

}
