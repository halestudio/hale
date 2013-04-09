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
import eu.esdihumboldt.hale.io.csv.writer.AbstractExportAlignment;
import eu.esdihumboldt.hale.io.csv.writer.CellInfo;

/**
 * Provider to write the alignment to a csv file
 * 
 * @author Patrick Lieb
 */
public class CSVAlignmentWriter extends AbstractExportAlignment {

	private List<Map<CellType, CellInfo>> mapping;

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

		CSVWriter writer = new CSVWriter(new OutputStreamWriter(getTarget().getOutput()));

		writer.writeNext(MAPPING_HEADER.toArray(new String[] {}));

		mapping = getMappingList();
		for (Map<CellType, CellInfo> entry : mapping) {
			// write each mapping to a row
			String[] row = new String[entry.size()];
			row[0] = getCellValue(entry, CellType.SOURCE_TYPE);
			row[1] = getCellValue(entry, CellType.SOURCE_TYPE_CONDITIONS);
			row[2] = getCellValue(entry, CellType.SOURCE_PROPERTIES);
			row[3] = getCellValue(entry, CellType.SOURCE_PROPERTY_CONDITIONS);
			row[4] = getCellValue(entry, CellType.TARGET_TYPE);
			row[5] = getCellValue(entry, CellType.TARGET_PROPERTIES);
			row[6] = getCellValue(entry, CellType.RELATION_NAME);
			row[7] = getCellValue(entry, CellType.CELL_EXPLANATION);
			row[8] = getCellValue(entry, CellType.CELL_NOTES);
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
		return "CSV HALE Alignment";
	}
}
