package eu.esdihumboldt.hale.ui.codelist.io;

import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReaderFactory;
import eu.esdihumboldt.hale.ui.io.ImportWizard;

/**
 * Import wizard for code lists
 * @author Patrick Lieb
 */
public class CodeListImportWizard extends ImportWizard<CodeListReader, CodeListReaderFactory> {
	
	/**
	 * Create a schema import wizard
	 */
	public CodeListImportWizard() {
		super(CodeListReaderFactory.class);
	}

}
