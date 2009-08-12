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

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.provider.TaskProviderFactory;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;

/**
 * This {@link Wizard} is used to import source and target schemas.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class SchemaImportWizard 
	extends Wizard 
	implements IImportWizard {
	
	private static Logger _log = Logger.getLogger(SchemaImportWizard.class);
	
	SchemaImportWizardMainPage mainPage;

	public SchemaImportWizard() {
		super();
		this.mainPage = new SchemaImportWizardMainPage(
				"Import Schema", "Import Schema"); //NON-NLS-1
		super.setWindowTitle("Schema Import Wizard"); //NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}
	
	

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		_log.debug("Wizard.canFinish: " + this.mainPage.isPageComplete());
		return this.mainPage.isPageComplete();
	}


	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		SchemaService schemaService = (SchemaService) 
					ModelNavigationView.site.getService(SchemaService.class);
		AlignmentService alService = (AlignmentService) 
					ModelNavigationView.site.getService(AlignmentService.class);
		
		try {
			File f = new File(mainPage.getResult());
			URI uri = f.toURI(); 
			if (mainPage.getSchemaType() == SchemaType.SOURCE) {;
				// load Schema as Source schema
				schemaService.loadSchema(uri, SchemaType.SOURCE);
				// update Alignment
				Schema schema = new Schema(schemaService.getSourceNameSpace(), 
						new Formalism("GML 3.2.1 Application Schema", 
								new URI("http://www.opengis.net/gml"))); // FIXME
				alService.getAlignment().setSchema1(schema);
			}
			else
			{
				schemaService.loadSchema(uri, SchemaType.TARGET);
				// update Alignment
				Schema schema = new Schema(schemaService.getTargetNameSpace(), 
						new Formalism("GML 3.2.1 Application Schema", 
								new URI("http://www.opengis.net/gml"))); // FIXME
				alService.getAlignment().setSchema2(schema);
			}
		} catch (Exception e2) {
			_log.error("Given Path/URL could not be parsed to an URI: ", e2);
		}
		
		// create tasks if checked.
		if (mainPage.createTasks()) {
			TaskService taskService = (TaskService) 
						ModelNavigationView.site.getService(TaskService.class);
			taskService.addTasks(
					TaskProviderFactory.getInstance().getTasks(
							schemaService.getSchema(mainPage.getSchemaType())));
		}

		return true;
	}
	 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
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
