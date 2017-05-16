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
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.csv.writer.AbstractAlignmentMappingExport;
import eu.esdihumboldt.hale.io.csv.writer.CellInformation;
import eu.esdihumboldt.hale.io.csv.writer.CellType;

/**
 * Provider to write the alignment to a csv file
 * 
 * @author Patrick Lieb
 */
public class CSVAlignmentMappingWriter extends AbstractAlignmentMappingExport {

	private List<Map<CellType, CellInformation>> mapping;

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

		// do initialization
		super.execute(progress, reporter);

		CSVWriter writer = new CSVWriter(new OutputStreamWriter(getTarget().getOutput()));

		writer.writeNext(getMappingHeader().toArray(new String[] {}));

		mapping = getMappingList();
		for (Map<CellType, CellInformation> entry : mapping) {
			// write each mapping to a row
			String[] row = new String[entry.size()];
			List<CellType> celltypes = getCellTypes();
			for (int i = 0; i < entry.size(); i++) {
				row[i] = getCellValue(entry, celltypes.get(i));
			}
			writer.writeNext(row);
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
		return "CSV hale Alignment";
	}
}
