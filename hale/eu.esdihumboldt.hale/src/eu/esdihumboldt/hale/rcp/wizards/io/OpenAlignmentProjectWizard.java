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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.ExceptionHelper;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OpenAlignmentProjectWizard 
	extends Wizard
	implements IExportWizard {
	
	private OpenAlignmentProjectWizardMainPage mainPage = null;
	
	private static ALogger _log = ALoggerFactory.getLogger(OpenAlignmentProjectWizard.class);
	
	/**
	 * Default constructor
	 */
	public OpenAlignmentProjectWizard() {
		super();
		this.mainPage = new OpenAlignmentProjectWizardMainPage(
				Messages.OpenAlignmentProjectWizard_OpenAlignmentProjectTitle, Messages.OpenAlignmentProjectWizard_OpenAlignmentProjectDescription); //NON-NLS-1
		super.setWindowTitle(Messages.OpenAlignmentProjectWizard_WindowTitle); //NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final String result = this.mainPage.getResult();
		if (result != null) {
			try {
				getContainer().run(true, false, new IRunnableWithProgress() {
					
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						ATransaction logTrans = _log.begin("Loading alignment project from " + result);
						try {
							ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
							ps.load(result, monitor);
						} catch (Exception e) {
							String message = Messages.OpenAlignmentProjectWizard_Failed;
							_log.userError(message, e);
//							ExceptionHelper.handleException(
//									message, HALEActivator.PLUGIN_ID, e);
						}
						finally {
							logTrans.end();
						}
					}
				});
			} catch (Exception e) {
				String message = Messages.OpenAlignmentProjectWizard_Failed2;
				_log.error(message, e);
				ExceptionHelper.handleException(
						message, HALEActivator.PLUGIN_ID, e);
			}
		}
		return true;
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// do nothing
	}

	/**
	 * @see IWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPage(this.mainPage);
	}

	/**
	 * @see IWizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return this.mainPage.isPageComplete();
	}

}
