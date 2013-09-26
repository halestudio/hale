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
import java.io.OutputStream;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaWriter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * XXX JSON schema writer stub.
 * 
 * @author Simon Templer
 */
public class JsonSchemaWriter extends AbstractSchemaWriter {

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generating JSON schema", ProgressIndicator.UNKNOWN);
		try (OutputStream out = getTarget().getOutput()) {

			// iterate over the schema types classified as mapping relevant
			for (TypeDefinition type : getSchemas().getMappingRelevantTypes()) {
				// do something
				// and write JSON schema to out
			}

			reporter.setSuccess(true);
//		} catch (Exception e) {
//			reporter.error(new IOMessageImpl(message, e));
		} finally {
			progress.end();
		}
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "JSON schema";
	}

}
