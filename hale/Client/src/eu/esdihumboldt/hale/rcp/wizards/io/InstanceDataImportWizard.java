/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.wizards.io;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;

/**
 * This {@link Wizard} controls the import of geodata to be used for 
 * visualisation of the transformations and for validation of their 
 * correctness.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class InstanceDataImportWizard 
	extends Wizard implements IImportWizard {

	private static Logger _log = Logger.getLogger(InstanceDataImportWizard.class);

	InstanceDataImportWizardMainPage mainPage;
	InstanceDataImportWizardFilterPage filterPage;
	InstanceDataImportWizardVerificationPage verificationPage;

	public InstanceDataImportWizard() {
		super();
		this.mainPage = new InstanceDataImportWizardMainPage(
				"Import Instance Data", "Import Geodata"); // NON-NLS-1
		this.filterPage = new InstanceDataImportWizardFilterPage(
				"Filter Instance Data to be imported",
				"Filter imported Geodata"); // NON-NLS-1
		this.verificationPage = new InstanceDataImportWizardVerificationPage(
				"Define Constraints for Data to be used in Transformation Verification",
				"Define Verification Constraints"); // NON-NLS-1
		super.setWindowTitle("Instance Data Import Wizard"); // NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return this.mainPage.isPageComplete();
	}

	/**
	 * Load instance data from source into {@link InstanceService}.
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		InstanceService instanceService = (InstanceService) ModelNavigationView.site
				.getService(InstanceService.class);
		return true;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		super.addPage(this.mainPage);
//		super.addPage(this.filterPage);
//		super.addPage(this.verificationPage);
	}
}
