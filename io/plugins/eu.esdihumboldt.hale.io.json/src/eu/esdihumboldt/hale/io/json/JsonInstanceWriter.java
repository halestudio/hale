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
 * Writes instances as JSON.
 * 
 * @author Sebastian Reinhardt
 */
public class JsonInstanceWriter extends AbstractInstanceWriter {

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generating JSON", ProgressIndicator.UNKNOWN);
		try {
			JacksonMapper mapper = new JacksonMapper();
			mapper.streamWriteCollection(getTarget(), getInstances(), reporter);

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Error generating JSON file", e));
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
