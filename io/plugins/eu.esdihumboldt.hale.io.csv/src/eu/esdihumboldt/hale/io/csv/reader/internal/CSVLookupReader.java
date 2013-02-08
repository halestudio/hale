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
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;
import eu.esdihumboldt.hale.common.lookup.impl.AbstractLookupImport;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableInfoImpl;

/**
 * The csv lookup reader class
 * 
 * @author Dominik Reuter
 */
public class CSVLookupReader extends AbstractLookupImport {

	/**
	 * the parameter specifying the reader setting
	 */
	public static final String PARAM_SKIP_FIRST_LINE = "skip";

	/**
	 * 
	 */
	public static final String FIRST_COLUMN = "First Column";
	/**
	 * 
	 */
	public static final String SECOND_COLUMN = "Second Column";

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

		int fColumn = Integer.parseInt(getParameter(FIRST_COLUMN).getStringRepresentation());
		int sColumn = Integer.parseInt(getParameter(SECOND_COLUMN).getStringRepresentation());

		boolean skipFirst = getParameter(PARAM_SKIP_FIRST_LINE).as(Boolean.class);
		CSVReader reader = CSVUtil.readFirst(this);
		String[] nextLine;
		if (skipFirst) {
			nextLine = reader.readNext();
		}

		Map<Value, Value> values = new HashMap<Value, Value>();
		while ((nextLine = reader.readNext()) != null) {
			values.put(Value.of(nextLine[fColumn]), Value.of(nextLine[sColumn]));
		}
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
