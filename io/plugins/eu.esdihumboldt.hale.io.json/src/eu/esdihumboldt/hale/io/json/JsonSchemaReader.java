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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.io.json.internal.IgnoreNamespaces;
import eu.esdihumboldt.hale.io.json.internal.JsonInstanceProcessor;
import eu.esdihumboldt.hale.io.json.internal.JsonReadMode;
import eu.esdihumboldt.hale.io.json.internal.NamespaceManager;
import eu.esdihumboldt.hale.io.json.internal.schema.JsonToSchema;

/**
 * Reader for a schema from a Json/GeoJson data file.
 * 
 * @author Simon Templer
 */
public class JsonSchemaReader extends AbstractSchemaReader {

	private static final ALogger log = ALoggerFactory.getLogger(JsonSchemaReader.class);

	/**
	 * Name of the parameter that specifies the read mode.
	 */
	public static final String PARAM_READ_MODE = "mode";

	private Schema schema;

	/**
	 * Default constructor
	 */
	public JsonSchemaReader() {
		super();

		addSupportedParameter(PARAM_READ_MODE);
	}

	@Override
	public boolean isCancelable() {
		// actual
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Attempting to read " + getDefaultTypeName(), ProgressIndicator.UNKNOWN);

		try {
			boolean expectGeoJson = true; // currently defaults to true, no
											// major difference in functionality

			// local name for type (unless there are @type notations in the
			// data)
			// TODO allow custom value through configuration
			String typeNameHint = "Json";

			// try to determine from file name
			URI loc = getSource().getLocation();
			if (loc != null) {
				String candidate = loc.getPath();
				if (candidate != null && !candidate.isEmpty()) {
					int lastSlash = candidate.lastIndexOf('/');
					if (lastSlash >= 0 && lastSlash + 1 < candidate.length()) {
						candidate = candidate.substring(lastSlash + 1);
					}
					if (candidate.toLowerCase().endsWith(".json")) {
						candidate = candidate.substring(0, candidate.length() - 5);
					}
					typeNameHint = candidate.replaceAll("[^\\w-_]", "_");
				}
			}

			// currently no specific namespace support
			NamespaceManager namespaces = new IgnoreNamespaces();

			JsonToSchema translator = new JsonToSchema(getReadMode(), expectGeoJson, typeNameHint,
					namespaces, SimpleLog.fromLogger(log), getSharedTypes());
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(getSource().getInput(), getCharset()))) {
				JsonInstanceProcessor.process(reader, translator, null,
						// TODO configurable limit
						50);
			}
			this.schema = translator.getSchema();

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
		if (mode == null) {
			setParameter(PARAM_READ_MODE, Value.NULL);
		}
		else {
			setParameter(PARAM_READ_MODE, Value.of(mode.toString()));
		}
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
		return "Schema from JSON";
	}

	@Override
	public Schema getSchema() {
		return schema;
	}

}
