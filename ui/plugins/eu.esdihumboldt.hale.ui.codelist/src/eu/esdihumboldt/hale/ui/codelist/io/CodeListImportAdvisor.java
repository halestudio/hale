package eu.esdihumboldt.hale.ui.codelist.io;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.ui.codelist.service.CodeListService;
import eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor;

/**
 * Import advisor for code lists
 * @author Patrick Lieb
 */
public class CodeListImportAdvisor extends DefaultIOAdvisor<CodeListReader> {

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(CodeListReader provider) {
		CodeList code = provider.getCodeList();
		
		CodeListService cs = (CodeListService) PlatformUI.getWorkbench().getService(CodeListService.class);
		cs.addCodeList(code);
		
		super.handleResults(provider);
	}

}
