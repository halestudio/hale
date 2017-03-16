/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.schema.persist.hsd.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.OsgiClassResolver;

/**
 * Reads the HALE schema model from JSON (HALE Schema Definition).
 * 
 * @author Simon Templer
 */
public class HaleSchemaReaderJson extends AbstractSchemaReader {

	private Schema schema;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Load schema", ProgressIndicator.UNKNOWN);
		try (InputStream in = getSource().getInput();
				Reader reader = new InputStreamReader(in, getCharset())) {
			Iterable<Schema> schemas = new JsonToSchema(null, new OsgiClassResolver(), reporter)
					.parseSchemas(reader);

			Iterator<Schema> it = schemas.iterator();
			if (it.hasNext()) {
				schema = it.next();
				reporter.setSuccess(true);
			}
			else {
				reporter.setSuccess(false);
				reporter.setSummary("No schema definition found");
			}

			if (it.hasNext()) {
				// FIXME what about multiple schemas???
			}
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "HALE Schema Definition (JSON)";
	}

	@Override
	public Schema getSchema() {
		return schema;
	}

}
