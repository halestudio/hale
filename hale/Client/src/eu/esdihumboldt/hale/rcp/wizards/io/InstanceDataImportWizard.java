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

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.geotools.feature.FeatureCollection;
import org.geotools.gml3.ApplicationSchemaConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;

/**
 * This {@link Wizard} controls the import of geodata to be used for 
 * visualisation of the transformations and for validation of their 
 * correctness.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class InstanceDataImportWizard 
	extends Wizard implements IImportWizard {

	private static Logger _log = Logger.getLogger(InstanceDataImportWizard.class);

	InstanceDataImportWizardMainPage mainPage;
	InstanceDataImportWizardFilterPage filterPage;
	InstanceDataImportWizardVerificationPage verificationPage;

	public InstanceDataImportWizard() {
		super();
		this.mainPage = new InstanceDataImportWizardMainPage(
				"Import Instance Data", "Import Geodata"); // NON-NLS-1
		this.filterPage = new InstanceDataImportWizardFilterPage(
				"Filter Instance Data to be imported",
				"Filter imported Geodata"); // NON-NLS-1
		this.verificationPage = new InstanceDataImportWizardVerificationPage(
				"Define Constraints for Data to be used in Transformation Verification",
				"Define Verification Constraints"); // NON-NLS-1
		super.setWindowTitle("Instance Data Import Wizard"); // NON-NLS-1
		super.setNeedsProgressMonitor(true);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return this.mainPage.isPageComplete();
	}

	/**
	 * Load instance data from source into {@link InstanceService}.
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		// get service references.
		InstanceService instanceService = (InstanceService) ModelNavigationView.site
				.getService(InstanceService.class);
		SchemaService schemaService = (SchemaService) ModelNavigationView.site
				.getService(SchemaService.class);
		
		// retrieve required parameters, specifically the location and the namespace of the source schema.
		String namespace = schemaService.getSourceNameSpace();
		if (namespace == null) {
			// set a default namespace
			namespace = "http://xsdi.org/default";
		}
		URL schema_location = schemaService.getSourceURL();
		if (schema_location == null) {
			throw new RuntimeException("You have to load a Schema first.");
		}
		
		// retrieve and parse result from the Wizard.
		URL gml_location = null;
		try {
			gml_location = new URL("file://" + this.mainPage.getResult());
		} catch (MalformedURLException e) {
			// it is ensured that only a valid URL is passed before
			throw new RuntimeException(this.mainPage.getResult()
					+ " was not parsed as a URL sucessfully: ", e);
		}
		InstanceInterfaceType iit = this.mainPage.getInterfaceType();
		
		// build FeatureCollection from the selected source.
		FeatureCollection<? extends FeatureType, ? extends Feature> features = null;
		
		if (iit.equals(InstanceInterfaceType.FILE)) {
			// FIXME handle shapefiles in addition to GML
			features = this.parseGML(namespace, schema_location, gml_location);
		}
		else if (iit.equals(InstanceInterfaceType.WFS)) {
			
		}
		if (features != null) {
			instanceService.addInstances(DatasetType.transformed, features);
			_log.info(features.size() + " instances were added to the InstanceService.");
		}
		return true;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		super.addPage(this.mainPage);
//		super.addPage(this.filterPage);
//		super.addPage(this.verificationPage);
	}
	
	/**
	 * This method allows to read a {@link FeatureCollection} from a given GML
	 * file.
	 * @param namespace the namespace to use in {@link FeatureType} creation.
	 * @param schema_location the {@link URL} of the schema to use in parsing.
	 * @param gml_location the {@link URL} identifying the GML file to parse.
	 * @return a {@link FeatureCollection}.
	 */
	private FeatureCollection<? extends FeatureType, ? extends Feature> parseGML(
			String namespace, URL schema_location, URL gml_location) {
		
		FeatureCollection<? extends FeatureType, ? extends Feature> result = null;
		try {
			Configuration configuration = new ApplicationSchemaConfiguration(
					namespace, 
					schema_location.toURI().getAuthority()
					+ schema_location.getPath());

			InputStream xml = new FileInputStream(gml_location.toURI()
					.getAuthority()
					+ gml_location.getPath());
			Parser parser = new Parser(configuration);
			// TODO start in a Thread of its own.
			result = 
				(FeatureCollection<? extends FeatureType, ? extends Feature>) 
					parser.parse(xml);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Parsing the given GML into a FeatureCollection failed: ",
					ex);
		}
		return result;
	}
}
