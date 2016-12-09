package eu.esdihumboldt.hale.io.codelist.skos.reader;

import java.io.IOException;
import java.net.URI;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * Reads a SKOS code list from rdf file or url
 * 
 * @author Arun
 */
public class SkosCodeListReader extends AbstractImportProvider implements CodeListReader {

	private CodeList codelist;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Loading SKOS code list", ProgressIndicator.UNKNOWN);
		try {
			URI loc = getSource().getLocation();

			codelist = new SkosCodeList(loc);
			progress.setCurrentTask("Code list loaded.");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		reporter.setSuccess(true);
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "SKOS code list";
	}

	@Override
	public CodeList getCodeList() {
		return codelist;
	}

}
