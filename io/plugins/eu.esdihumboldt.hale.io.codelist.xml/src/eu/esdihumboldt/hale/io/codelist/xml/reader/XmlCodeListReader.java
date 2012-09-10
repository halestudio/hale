/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
