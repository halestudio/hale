/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.deegree.mapping;

import java.io.IOException;

import eu.esdihumboldt.hale.common.config.ProviderConfig;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaWriter;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.io.deegree.mapping.config.GenericMappingConfiguration;

/**
 * Creates a deegree SQL mapping configuration from a schema.
 * 
 * @author Simon Templer
 */
public class MappingSchemaWriter extends AbstractSchemaWriter {

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		GenericMappingConfiguration config = new GenericMappingConfiguration(
				ProviderConfig.get(this));
		try {
			config.validate();
		} catch (Exception e) {
			throw new IOProviderConfigurationException(e);
		}
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generate deegree SQL mapping", ProgressIndicator.UNKNOWN);
		try {
			Schema targetSchema = getSchemas().getSchemas().iterator().next();
			GenericMappingConfiguration config = new GenericMappingConfiguration(
					ProviderConfig.get(this));
			MappingWriter writer = new MappingWriter(targetSchema, null, config);

			MappingAlignmentWriter.writeResult(writer, getTarget(), getContentType(), config);

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error("Error writing deegree SQL mapping", e);
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}

		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "deegree configuration";
	}

}
