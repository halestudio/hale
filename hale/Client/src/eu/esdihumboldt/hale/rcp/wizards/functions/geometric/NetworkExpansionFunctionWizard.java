package eu.esdihumboldt.hale.rcp.wizards.functions.geometric;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class NetworkExpansionFunctionWizard extends Wizard 
implements INewWizard {

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		System.out.println(activeWindow.getClass().getName());
		StructuredSelection structuredSelection = (StructuredSelection) activeWindow.getSelectionService().getSelection();
		//selection = (IStructuredSelection) workbench.getWorkbenchWindows()[0].getSelectionService().getSelection();
		System.out.println(structuredSelection.size());

		
	}

}
