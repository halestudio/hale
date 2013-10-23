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

package eu.esdihumboldt.hale.io.xls.reader;

import java.io.IOException;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;
import eu.esdihumboldt.hale.common.lookup.impl.AbstractLookupImport;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableInfoImpl;
import eu.esdihumboldt.hale.io.csv.writer.LookupTableExportConstants;

/**
 * Reader for xls/xlsx lookup table files
 * 
 * @author Patrick Lieb
 */
public class XLSLookupTableReader extends AbstractLookupImport {

	private LookupTableInfo lookupTable;

	/**
	 * @see eu.esdihumboldt.hale.common.lookup.LookupTableImport#getLookupTable()
	 */
	@Override
	public LookupTableInfo getLookupTable() {
		return lookupTable;
	}

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

		int keyColumn = getParameter(LookupTableExportConstants.LOOKUP_KEY_COLUMN)
				.as(Integer.class);
		int valueColumn = getParameter(LookupTableExportConstants.LOOKUP_VALUE_COLUMN).as(
				Integer.class);

		boolean skipFirst = getParameter(LookupTableExportConstants.PARAM_SKIP_FIRST_LINE).as(
				Boolean.class);

		Workbook workbook;
		// write xls file
		if (getContentType().getId().equals("eu.esdihumboldt.hale.io.xls.xls")) {
			workbook = new HSSFWorkbook(getSource().getInput());
		}
		// write xlsx file
		else if (getContentType().getId().equals("eu.esdihumboldt.hale.io.xls.xlsx")) {
			workbook = new XSSFWorkbook(getSource().getInput());
		}
		else {
			reporter.setSuccess(false);
			return reporter;
		}

		DefaultXLSLookupTableReader reader = new DefaultXLSLookupTableReader();

		Map<Value, Value> map = reader.read(workbook, skipFirst, keyColumn, valueColumn);

		lookupTable = new LookupTableInfoImpl(new LookupTableImpl(map), getName(), getDescription());

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

}
