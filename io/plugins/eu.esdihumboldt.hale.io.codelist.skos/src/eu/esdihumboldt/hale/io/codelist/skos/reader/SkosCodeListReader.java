package eu.esdihumboldt.hale.io.codelist.skos.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;

/**
 * Reads a SKOS code list from rdf file or url
 * 
 * @author Arun
 */
public class SkosCodeListReader extends AbstractImportProvider implements CodeListReader {

	private CodeList codelist;
	private String language;

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Loading SKOS code list", ProgressIndicator.UNKNOWN);
		try {

			this.language = getLangauge();

			URI loc = getSource().getLocation();
			InputStream in = getSource().getInput();
			codelist = new SkosCodeList(in, loc, this.language);
			progress.setCurrentTask("Code list loaded.");
			reporter.setSuccess(true);
		} catch (Exception ex) {
			reporter.error(new IOMessageImpl("Error in loading skos code list", ex));
			reporter.setSuccess(false);
		}
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

	/**
	 * Get language. By default Locale language is used.
	 * 
	 * @return String
	 */
	public String getLangauge() {
		return Locale.getDefault().getLanguage();
	}

}
