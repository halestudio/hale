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
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;
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
	
	private static ALogger _log = ALoggerFactory.getLogger(MappingExportWizard.class);
	
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
		final String format = this.mainPage.getSelectedFormatName();
		final MappingExportProvider mef = MappingExportExtension.getExportProvider(format);
		String extension = MappingExportExtension.getRegisteredExportProviderInfo().get(format);
		extension = extension.substring(1);
		if (path != null) {
			if (!path.endsWith(extension)) {
				path = path + extension;
			}
			
			final String file = path;
			try {
				getContainer().run(true, false, new IRunnableWithProgress() {
					
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						AlignmentService alService = (AlignmentService) 
						PlatformUI.getWorkbench().getService(AlignmentService.class);
						Alignment al = alService.getAlignment();
				
						SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
						
						ATransaction trans = _log.begin("Exporting mapping");
						try {
							//TODO instead give the monitor to the exporter? support for canceling?
							monitor.beginTask("Exporting mapping", IProgressMonitor.UNKNOWN);
							mef.export(al, file, schemaService.getSourceSchema(), 
									schemaService.getTargetSchema());
						} catch (Throwable e) {
							String message = Messages.MappingExportWizard_SaveFailed;
							_log.userError(message + " " + e.getMessage(), e);
						} finally {
							monitor.done();
							trans.end();
						}
					}
				});
			} catch (Exception e) {
				_log.userError("Error starting mapping export process", e);
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
