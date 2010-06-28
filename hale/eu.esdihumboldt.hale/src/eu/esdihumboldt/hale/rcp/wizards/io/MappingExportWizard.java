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

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.ExceptionHelper;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportExtension;
import eu.esdihumboldt.hale.rcp.wizards.io.mappingexport.MappingExportProvider;

/**
 * This wizard is used to export the currently active mapping to an gOML file.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MappingExportWizard 
	extends Wizard
	implements IExportWizard {
	
	private MappingExportWizardMainPage mainPage;
	
	private static Logger _log = Logger.getLogger(MappingExportWizard.class);
	
	/**
	 * Default constructor
	 */
	public MappingExportWizard() {
		super();
		this.mainPage = new MappingExportWizardMainPage(
				Messages.MappingExportWizard_ExportMenu1, Messages.MappingExportWizard_ExportMenu2); //NON-NLS-1
		super.setWindowTitle(Messages.MappingExportWizard_WindowTitle); //NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}
	
	/**
	 * @see IWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		String path = this.mainPage.getResult();
		String format = this.mainPage.getSelectedFormatName();
		MappingExportProvider mef = MappingExportExtension.getExportProvider(format);
		String extension = MappingExportExtension.getRegisteredExportProviderInfo().get(format);
		extension = extension.substring(1);
		if (path != null) {
			if (!path.endsWith(extension)) {
				path = path + extension;
			}
			AlignmentService alService = (AlignmentService) 
					PlatformUI.getWorkbench().getService(AlignmentService.class);
			Alignment al = alService.getAlignment();
			
			SchemaService schemaService = (SchemaService) 
					PlatformUI.getWorkbench().getService(SchemaService.class);
			
			try {
				mef.export(al, path, schemaService.getSourceSchema(), 
						schemaService.getTargetSchema());
			} catch (Exception e) {
				String message = Messages.MappingExportWizard_SaveFailed;
				_log.error(message + ". " + e.getMessage() + ". " 
						+ e.getCause().getMessage(), e);
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
