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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.ExceptionHelper;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * This {@link Wizard} is used to import source and target schemas.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @version $Id$
 */
public class SchemaImportWizard 
	extends Wizard 
	implements IImportWizard {
	
	private static ALogger _log = ALoggerFactory.getLogger(SchemaImportWizard.class);
	
	SchemaImportWizardMainPage mainPage;

	/**
	 * Default constructor
	 */
	public SchemaImportWizard() {
		super();
		this.mainPage = new SchemaImportWizardMainPage(
				Messages.SchemaImportWizard_ImportSchemaTitle, Messages.SchemaImportWizard_ImportSchemaDescription); //NON-NLS-1
		super.setWindowTitle(Messages.SchemaImportWizard_WindowTitle); //NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}
	
	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete()); //$NON-NLS-1$
		return this.mainPage.isPageComplete();
	}


	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final String result = mainPage.getResult();
		final SchemaType schemaType = mainPage.getSchemaType();
		
		final boolean useWfs = mainPage.useWfs();
		
		final AtomicBoolean succeeded = new AtomicBoolean(true);
		
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask(Messages.SchemaImportWizard_SchemaImport, IProgressMonitor.UNKNOWN);
					
					SchemaService schemaService = (SchemaService) 
								PlatformUI.getWorkbench().getService(SchemaService.class);
					AlignmentService alService = (AlignmentService) 
								PlatformUI.getWorkbench().getService(AlignmentService.class);
					final ProjectService projectService = (ProjectService) 
								PlatformUI.getWorkbench().getService(ProjectService.class);
					
					try {
						URI uri = getSchemaURI(result);
						
						Collection<SchemaElement> currentSchema = schemaService.getSchema(schemaType);
						if (currentSchema != null && !currentSchema.isEmpty()) {
							final String title = ((schemaType == SchemaType.SOURCE)?(Messages.SchemaImportWizard_1):(Messages.SchemaImportWizard_2));
							final String message = ((schemaType == SchemaType.SOURCE)
									?(Messages.SchemaImportWizard_3)
									:(Messages.SchemaImportWizard_4));
							
							final AtomicBoolean loadSchema = new AtomicBoolean(false);
							final Display display = PlatformUI.getWorkbench().getDisplay();
							display.syncExec(new Runnable() {
								
								@Override
								public void run() {
									if (MessageDialog.openQuestion(getShell(), 
											title,
											message)) {
										loadSchema.set(true);
									}
								}
							});
							
							if (!loadSchema.get()) {
								succeeded.set(false);
								return;
							}
						}
						
						ProgressIndicator progress = new ProgressIndicator() {
							
							@Override
							public void begin(String taskName,
									int totalWork) {
								// ignore
							}

							@Override
							public void end() {
								// ignore
							}

							@Override
							public boolean isCanceled() {
								return false;
							}

							@Override
							public void advance(int workUnits) {
								// ignore
							}
							
							@Override
							public void setCurrentTask(String taskName) {
								monitor.subTask(taskName);
							}
						};
						
						String customSchemaFormat = null;
						if (useWfs) {
							customSchemaFormat = "xsd"; //$NON-NLS-1$
						}
						
						if (schemaType == SchemaType.SOURCE) {
							// load Schema as Source schema
							schemaService.loadSchema(uri, customSchemaFormat, 
									SchemaType.SOURCE, progress);
							// update Alignment
							Schema schema = new Schema(null, null);
							schema.setLocation(uri.toASCIIString());
							schema.setAbout(new About(schemaService.getSourceNameSpace()));
							schema.setFormalism(new Formalism(
									"GML Application Schema",  //FIXME Use predefined formalism objects //$NON-NLS-1$
									new URI("http://www.opengis.net/gml"))); //$NON-NLS-1$
							
							alService.getAlignment().setSchema1(schema);
							projectService.setSourceSchemaPath(uri.toString());
						}
						else
						{
							schemaService.loadSchema(uri, customSchemaFormat, 
									SchemaType.TARGET, progress);
							// update Alignment
							Schema schema = new Schema(null, null);
							schema.setLocation(uri.toASCIIString());
							schema.setAbout(new About(schemaService.getTargetNameSpace()));
							schema.setFormalism(new Formalism(
									"GML Application Schema",  //FIXME Use predefined formalism objects //$NON-NLS-1$
									new URI("http://www.opengis.net/gml"))); //$NON-NLS-1$
							alService.getAlignment().setSchema2(schema);
							projectService.setTargetSchemaPath(uri.toString());
						}
					} catch (Exception e2) {
						_log.userError(Messages.SchemaImportWizard_0, e2);
//						ExceptionHelper.handleException(
//								Messages.SchemaImportWizard_ErrorMessage1, 
//								HALEActivator.PLUGIN_ID, e2);
						succeeded.set(false);
					}
					
					monitor.done();
				}
				
			});
		} catch (Exception e) {
			ExceptionHelper.handleException(
					Messages.SchemaImportWizard_JobError, HALEActivator.PLUGIN_ID, e);
		}
		
		return succeeded.get();
	}
	 
	/**
	 * Get the schema {@link URI} for the given location
	 * 
	 * @param location the schema location
	 * @return the {@link URI}
	 */
	private URI getSchemaURI(String location) {
		// check if it is an existing file
		File file = new File(location);
		if (file.exists()) {
			return file.toURI();
		}
		// else it should be a URL
		else {
			return URI.create(location);
		}
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
        super.addPages(); 
        addPage(mainPage);        
    }

}
