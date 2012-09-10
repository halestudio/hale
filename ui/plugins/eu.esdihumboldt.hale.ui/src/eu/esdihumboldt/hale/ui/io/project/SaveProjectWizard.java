/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.io.project;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Wizard for saving a project
 * 
 * @author Simon Templer
 */
public class SaveProjectWizard extends ExportWizard<ProjectWriter> {

	/**
	 * Advisor identifier for saving a project
	 */
	public static final String ADVISOR_PROJECT_SAVE = "project.save";

	/**
	 * Create a wizard that saves a project
	 */
	public SaveProjectWizard() {
		super(ProjectWriter.class);
	}

	/**
	 * @see ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		addPage(new SaveProjectDetailsPage());
	}

	/**
	 * @see IOWizard#updateConfiguration(IOProvider)
	 */
	@Override
	protected void updateConfiguration(ProjectWriter provider) {
		super.updateConfiguration(provider);

		// project has been set and can be adapted

		// populate and set the save configuration
		IOConfiguration saveConfiguration = new IOConfiguration();
		saveConfiguration.setActionId(ADVISOR_PROJECT_SAVE);
		saveConfiguration.setProviderId(getProviderFactory().getIdentifier());
		provider.storeConfiguration(saveConfiguration.getProviderConfiguration());
		provider.getProject().setSaveConfiguration(saveConfiguration);
	}

}
