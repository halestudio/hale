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

import java.io.IOException;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;

/**
 * Writes instances as GeoJSON.
 * 
 * @author Kai Schwierczek
 */
public class GeoJSONInstanceWriter extends AbstractInstanceWriter {

	/**
	 * Parameter name for the default geometry association.
	 */
	public static final String PARAM_GEOMETRY_CONFIG = "geojson.geometry.config";

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generating GeoJSON", ProgressIndicator.UNKNOWN);
		try {
			JacksonMapper mapper = new JacksonMapper();
			GeoJSONConfig config = getParameter(PARAM_GEOMETRY_CONFIG).as(GeoJSONConfig.class,
					new GeoJSONConfig());
			mapper.streamWriteGeoJSONCollection(getTarget(), getInstances(), config, reporter);

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Error generating GeoJSON file", e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}

		return reporter;
	}

	@Override
	public boolean isPassthrough() {
		return true;
	}

	@Override
	protected String getDefaultTypeName() {
		return "GeoJSON";
	}

}
