package eu.esdihumboldt.hale.io.appschema.ui;

import eu.esdihumboldt.hale.io.appschema.writer.AbstractAppSchemaConfigurator;
import eu.esdihumboldt.hale.ui.io.ExportWizard;

/**
 * 
 * Wizard for exporting alignments as app-schema mapping configurations.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaAlignmentExportWizard extends ExportWizard<AbstractAppSchemaConfigurator> {

	/**
	 * Default constructor.
	 */
	public AppSchemaAlignmentExportWizard() {
		super(AbstractAppSchemaConfigurator.class);
	}

	@Override
	public void addPages() {
		// add the datastore configuration page
		addPage(new AppSchemaDataStoreConfigurationPage());
		super.addPages();
	}
}
