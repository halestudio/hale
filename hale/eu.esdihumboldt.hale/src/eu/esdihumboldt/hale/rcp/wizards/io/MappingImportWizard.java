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

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;

/**
 * This wizard is used to export the currently active mapping to an gOML file.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MappingImportWizard 
	extends Wizard
	implements IImportWizard {
	
	private MappingImportWizardMainPage mainPage;
	
	private static Logger _log = Logger.getLogger(MappingImportWizard.class);
	
	/**
	 * Default constructor
	 */
	public MappingImportWizard() {
		super();
		this.mainPage = new MappingImportWizardMainPage(
				Messages.MappingImportWizard_ImportMappingTitle, Messages.MappingImportWizard_ImportMappingDescription); //NON-NLS-1
		super.setWindowTitle(Messages.MappingImportWizard_WindowTitle); //NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}
	
	/**
	 * @see IWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		String result = this.mainPage.getResult();
		if (result != null) {
			final Display display = Display.getCurrent();
			
			File file = new File(result);
			if (!file.exists()) {
				MessageDialog.openError(display.getActiveShell(), 
						Messages.MappingImportWizard_FileExistTitle, Messages.MappingImportWizard_FileExistDescription);
				return false;
			}
			
			// load alignment.
			AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
			try {
				OmlRdfReader reader = new OmlRdfReader();
				Alignment alignment = reader.read(result);
				
				if (alignment != null) {
					String sourceNamespace = alignment.getSchema1().getAbout().getAbout();
					SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
					if (!sourceNamespace.equals(schemaService.getSourceNameSpace())) {
						MessageDialog.openWarning(display.getActiveShell(), 
								Messages.MappingImportWizard_SourceExistTitle, 
								Messages.MappingImportWizard_SourceExistDescription + sourceNamespace);
						return false;
					}
					
					String targetNamespace = alignment.getSchema2().getAbout().getAbout();
					if (!targetNamespace.equals(schemaService.getTargetNameSpace())) {
						MessageDialog.openWarning(display.getActiveShell(), 
								Messages.MappingImportWizard_TargetExistTitle, 
								Messages.MappingImportWizard_TargetExistDescription + targetNamespace);
						return false;
					}
					
					_log.info("Number of loaded cells: " + alignment.getMap().size()); //$NON-NLS-1$
					
					alignmentService.addOrUpdateAlignment(alignment);
				}
			} catch (Exception e) {
				throw new RuntimeException("Alignment could not be loaded: ", e); //$NON-NLS-1$
			}
		}
		return true;
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
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
