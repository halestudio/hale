/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.io;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * This is the supertype to extend for adding Wizards for transformation 
 * functions offered by the CST.
 * 
 * @author Anna Pitaev 
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class FunctionWizard 
	extends Wizard 
	implements IImportWizard {
	
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
