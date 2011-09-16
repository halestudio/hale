package eu.esdihumboldt.hale.codelist.ui.io;

import org.eclipse.ui.PlatformUI;


import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.codelist.CodeList;
import eu.esdihumboldt.hale.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.codelist.ui.service.CodeListService;

/**
 * Import advisor for code lists
 * @author Patrick Lieb
 */
public class CodeListImportAdvisor extends AbstractIOAdvisor<CodeListReader> {

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(CodeListReader provider) {
		
		CodeList code = provider.getCodeList();
		
		CodeListService cs = (CodeListService) PlatformUI.getWorkbench().getService(CodeListService.class);
		cs.addCodeList(code);
	}

}
