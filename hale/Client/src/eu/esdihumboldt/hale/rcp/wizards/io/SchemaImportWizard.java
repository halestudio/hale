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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.impl.SchemaServiceImpl;
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
	
	SchemaImportWizardPage mainPage;

	public SchemaImportWizard() {
		super();
		this.mainPage = new SchemaImportWizardPage(
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
		try {
			schemaService.loadSourceSchema(
					new URI(mainPage.getResult().replaceAll("\\\\", "/")));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return true;
	}
	 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		_log.debug("in init...");
	}
	
	/**
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(mainPage);        
    }

}
