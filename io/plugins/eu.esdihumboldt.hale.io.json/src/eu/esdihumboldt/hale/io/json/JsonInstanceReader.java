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
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.json.internal.JsonInstanceCollection;
import eu.esdihumboldt.hale.io.json.internal.JsonReadMode;
import eu.esdihumboldt.hale.io.json.internal.JsonToInstance;

/**
 * Reader for Json/GeoJson data.
 * 
 * @author Simon Templer
 */
public class JsonInstanceReader extends AbstractInstanceReader {

	private static final ALogger log = ALoggerFactory.getLogger(JsonInstanceReader.class);

	/**
	 * Name of the parameter that specifies the read mode.
	 */
	public static final String PARAM_READ_MODE = "mode";

	private InstanceCollection instances;

	/**
	 * Default constructor
	 */
	public JsonInstanceReader() {
		super();

		addSupportedParameter(PARAM_READ_MODE);
	}

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

			JsonToInstance translator = new JsonToInstance(getReadMode(), expectGeoJson, type,
					getSourceSchema(), SimpleLog.fromLogger(log));
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

	/**
	 * Set the read mode to use.
	 * 
	 * @param mode the mode for reading Json
	 */
	public void setReadMode(JsonReadMode mode) {
		setParameter(PARAM_READ_MODE, Value.of(mode.toString()));
	}

	/**
	 * @return the mode to use for reading Json
	 */
	public JsonReadMode getReadMode() {
		JsonReadMode value = getParameter(PARAM_READ_MODE).as(JsonReadMode.class);
		if (value == null)
			return JsonReadMode.auto;
		else
			return value;
	}

	@Override
	protected String getDefaultTypeName() {
		return "JSON";
	}

}
