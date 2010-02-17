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
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import eu.esdihumboldt.hale.models.project.ProjectGenerator;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.ExceptionHelper;

/**
 * TODO Explain the purpose of this type here.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class SaveAlignmentProjectWizard 
	extends Wizard
	implements IExportWizard {
	
	private SaveAlignmentProjectWizardMainPage mainPage;
	
	private static Logger _log = Logger.getLogger(SaveAlignmentProjectWizard.class);
	
	/**
	 * Default constructor
	 */
	public SaveAlignmentProjectWizard() {
		super();
		this.mainPage = new SaveAlignmentProjectWizardMainPage(
				Messages.SaveAlignmentProjectWizard_SaveAlignmentTitle, Messages.SaveAlignmentProjectWizard_SaveAlignmentDescription); //NON-NLS-1
		super.setWindowTitle(Messages.SaveAlignmentProjectWizard_WindowTitle); //NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		String result = this.mainPage.getResult();
		if (result != null) {
			try {
				ProjectGenerator.write(result, "default"); //$NON-NLS-1$
			} catch (Exception e) {
				String message = Messages.SaveAlignmentProjectWizard_SaveFaild;
				_log.error(message, e);
				ExceptionHelper.handleException(
						message, HALEActivator.PLUGIN_ID, e);
			}
		}
		return true;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPage(this.mainPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#canFinish()
	 */
	public boolean canFinish() {
		return this.mainPage.isPageComplete();
	}

}
