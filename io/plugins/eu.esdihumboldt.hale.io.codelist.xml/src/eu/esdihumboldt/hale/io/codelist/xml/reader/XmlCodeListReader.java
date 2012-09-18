/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.codelist.xml.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * Reads a code list based on XML
 * 
 * @author Patrick Lieb
 */
public class XmlCodeListReader extends AbstractImportProvider implements CodeListReader {

	private CodeList codelist;

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {

		return false;
	}

	/**
	 * @see CodeListReader#getCodeList()
	 */
	@Override
	public CodeList getCodeList() {

		return codelist;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Loading code list.", ProgressIndicator.UNKNOWN);
		try {
			InputStream in = getSource().getInput();
			URI loc = getSource().getLocation();
			codelist = new XmlCodeList(in, loc);
			progress.setCurrentTask("Code list loaded.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XML code list";
	}

}
