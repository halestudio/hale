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

package eu.esdihumboldt.hale.io.csv.writer.internal;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.lookup.impl.AbstractLookupExport;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConstants;
import eu.esdihumboldt.hale.io.csv.writer.LookupTableExportConstants;

/**
 * Writer to export lookup tables as csv files
 * 
 * @author Patrick Lieb
 */
public class CSVLookupWriter extends AbstractLookupExport {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		String separator = getParameter(CSVConstants.PARAM_SEPARATOR).as(String.class);
		String quote = getParameter(CSVConstants.PARAM_QUOTE).as(String.class);
		String escape = getParameter(CSVConstants.PARAM_ESCAPE).as(String.class);

		String sourceColumn = getParameter(LookupTableExportConstants.PARAM_SOURCE_COLUMN).as(String.class);
		String targetColumn = getParameter(LookupTableExportConstants.PARAM_TARGET_COLUMN).as(String.class);

		// parameters have to be strings with only one char, so take the first
		// ones
		CSVWriter writer = new CSVWriter(new OutputStreamWriter(getTarget().getOutput()),
				separator.charAt(0), quote.charAt(0), escape.charAt(0));
		Map<Value, Value> table = getLookupTable().getTable().asMap();

		// write header first
		String[] values = new String[] { sourceColumn, targetColumn };
		writer.writeNext(values);

		for (Value key : table.keySet()) {
			values = new String[] { key.as(String.class), table.get(key).as(String.class) };
			writer.writeNext(values);
		}
		writer.close();
		reporter.setSuccess(true);

		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "CSV Lookup Table";
	}

}
