/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVUtil;
import eu.esdihumboldt.hale.io.csv.writer.AbstractTableInstanceWriter;

/**
 * Provider for exporting instances as csv files
 * 
 * @author Patrick
 */
public class CSVInstanceWriter extends AbstractTableInstanceWriter {

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
		super.execute(progress, reporter);

		CSVWriter writer = new CSVWriter(new OutputStreamWriter(getTarget().getOutput()),
				CSVUtil.getSep(this), CSVUtil.getQuote(this), CSVUtil.getEscape(this));

		List<List<Object>> table = getTable().get(0);

		for (int i = 0; i < table.size(); i++) {
			List<Object> row = table.get(i);
			String[] rowString = new String[row.size()];
			for (int k = 0; k < row.size(); k++) {
				rowString[k] = row.get(k).toString();
			}
			writer.writeNext(rowString);
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
		return "csv file";
	}

}
