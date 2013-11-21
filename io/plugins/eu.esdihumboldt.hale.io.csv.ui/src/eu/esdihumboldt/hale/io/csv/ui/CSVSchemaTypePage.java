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

package eu.esdihumboldt.hale.io.csv.ui;

import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVUtil;
import eu.esdihumboldt.hale.ui.HaleWizardPage;

/**
 * Schema type configuration page for loading csv schema files
 * 
 * @author Kevin Mais
 */
public class CSVSchemaTypePage extends DefaultSchemaTypePage {

	/**
	 * default constructor
	 */
	public CSVSchemaTypePage() {
		super("CSV.SchemaTypePage");
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {

		try {
			CSVReader reader = CSVUtil.readFirst(getWizard().getProvider());
			setHeader(reader.readNext());
			setSecondRow(reader.readNext());
			super.onShowPage(firstShow);
		} catch (IOException e) {
			setMessage("File cannot be loaded!", WARNING);
		}

	}

}
