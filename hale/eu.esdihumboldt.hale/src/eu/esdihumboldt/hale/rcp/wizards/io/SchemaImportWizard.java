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
import java.net.URI;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.provider.TaskProviderFactory;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * This {@link Wizard} is used to import source and target schemas.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @version $Id$
 */
public class SchemaImportWizard 
	extends Wizard 
	implements IImportWizard {
	
	private static Logger _log = Logger.getLogger(SchemaImportWizard.class);
	
	SchemaImportWizardMainPage mainPage;

	/**
	 * Default constructor
	 */
	public SchemaImportWizard() {
		super();
		this.mainPage = new SchemaImportWizardMainPage(
				"Import Schema", "Import Schema"); //NON-NLS-1
		super.setWindowTitle("Schema Import Wizard"); //NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}
	
	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete());
		return this.mainPage.isPageComplete();
	}


	/**
	 * @see Wizard#performFinish()
	 */
	public boolean performFinish() {
		SchemaService schemaService = (SchemaService) 
					PlatformUI.getWorkbench().getService(SchemaService.class);
		AlignmentService alService = (AlignmentService) 
					PlatformUI.getWorkbench().getService(AlignmentService.class);
		final ProjectService projectService = (ProjectService) 
					PlatformUI.getWorkbench().getService(ProjectService.class);
		
		try {
			final String result = mainPage.getResult();
			
			URI uri = getSchemaURI(result);
			
			Collection<TypeDefinition> currentSchema = schemaService.getSchema(mainPage.getSchemaType());
			if (currentSchema != null && !currentSchema.isEmpty()) {
				String info = ((mainPage.getSchemaType() == SchemaType.SOURCE)?("source"):("target"));
				
				if (!MessageDialog.openQuestion(getShell(), "Replace " + info + " schema",
						"A " + info + 
						" schema has already been loaded. Do you want to replace it with this schema?")) {
					return false;
				}
			}
			
			if (mainPage.getSchemaType() == SchemaType.SOURCE) {;
				// load Schema as Source schema
				schemaService.loadSchema(uri, SchemaType.SOURCE);
				// update Alignment
				Schema schema = new Schema(schemaService.getSourceNameSpace(), 
						new Formalism("GML 3.2.1 Application Schema", 
								new URI("http://www.opengis.net/gml"))); // FIXME
				alService.getAlignment().setSchema1(schema);
				projectService.setSourceSchemaPath(uri.toString());
			}
			else
			{
				schemaService.loadSchema(uri, SchemaType.TARGET);
				// update Alignment
				Schema schema = new Schema(schemaService.getTargetNameSpace(), 
						new Formalism("GML 3.2.1 Application Schema", 
								new URI("http://www.opengis.net/gml"))); // FIXME
				alService.getAlignment().setSchema2(schema);
				projectService.setTargetSchemaPath(uri.toString());
			}
		} catch (Exception e2) {
			_log.error("Given Path/URL could not be parsed to an URI: ", e2);
		}
		
		// create tasks if checked.
		if (mainPage.createTasks()) {
			TaskService taskService = (TaskService) 
						PlatformUI.getWorkbench().getService(TaskService.class);
			taskService.addTasks(
					TaskProviderFactory.getInstance().getTasks(
							schemaService.getSchema(mainPage.getSchemaType())));
		}

		return true;
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
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}
	
	/**
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(mainPage);        
    }

}
