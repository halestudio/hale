package eu.esdihumboldt.hale.rcp.wizards.io;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


public class FunctionWizard extends Wizard implements IImportWizard{
	
	SchemaImportWizardMainPage mainPage;
	
	public FunctionWizard() {
		super();
		this.mainPage = new SchemaImportWizardMainPage(
				"Select a wizard", "FunctionWizard");
		super.setWindowTitle("Function Wizard"); // NON-NLS-1
		super.setNeedsProgressMonitor(true);
		
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	System.out.println("in init");
		
	}

	@Override
	public void addPages() {
		super.addPages();
		super.addPage(this.mainPage);
		
	}

	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		
		return this.mainPage.isPageComplete();
	}

}
