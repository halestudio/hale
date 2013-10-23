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

package eu.esdihumboldt.hale.io.csv.reader.internal;

import java.io.IOException;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;
import eu.esdihumboldt.hale.common.lookup.impl.AbstractLookupImport;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableInfoImpl;
import eu.esdihumboldt.hale.io.csv.reader.DefaultCSVLookupReader;
import eu.esdihumboldt.hale.io.csv.writer.LookupTableExportConstants;

/**
 * The csv lookup reader class
 * 
 * @author Dominik Reuter
 */
public class CSVLookupReader extends AbstractLookupImport {

	/**
	 * The lookuptable
	 */
	private LookupTableInfo lookupTable;

	/**
	 * @see eu.esdihumboldt.hale.common.lookup.LookupTableImport#getLookupTable()
	 */
	@Override
	public LookupTableInfo getLookupTable() {
		if (lookupTable != null) {
			return lookupTable;
		}
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		// Nothing to do here
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

		DefaultCSVLookupReader reader = new DefaultCSVLookupReader();
		Map<Value, Value> values = reader.read(getSource().getInput(), getCharset(),
				CSVUtil.getSep(this), CSVUtil.getQuote(this), CSVUtil.getEscape(this), skipFirst,
				keyColumn, valueColumn);

		lookupTable = new LookupTableInfoImpl((new LookupTableImpl(values)), getName(),
				getDescription());
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		// Nothing to do here
		return null;
	}
}
