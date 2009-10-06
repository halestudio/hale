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

package eu.esdihumboldt.hale.rcp.commandHandlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.geotools.feature.FeatureCollection;
import org.geotools.gml3.ApplicationSchemaConfiguration;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.instance.HaleGMLParser;
import eu.esdihumboldt.hale.models.provider.TaskProviderFactory;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.ExceptionHelper;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.wizards.io.FunctionWizard;
import eu.esdihumboldt.hale.rcp.wizards.io.InstanceInterfaceType;
import eu.esdihumboldt.hale.rcp.wizards.io.SchemaImportWizard;

/**
 */
public class LoadTestDataHandler extends AbstractHandler implements IHandler {

	private static Logger _log = Logger.getLogger(LoadTestDataHandler.class);

	private void LoadSchemas(String sourceFilename, String targetFilename) {
		SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
		AlignmentService alService = (AlignmentService) PlatformUI.getWorkbench()
				.getService(AlignmentService.class);

		try {
			File fileSource = new File(sourceFilename);
			URI uriSource = fileSource.toURI();

			// load Schema as Source schema
			schemaService.loadSchema(uriSource, SchemaType.SOURCE);
			
			// update Alignment
			Schema schemaSource = new Schema(schemaService.getSourceNameSpace(),
						new Formalism("GML 3.2.1 Application Schema", new URI(
								"http://www.opengis.net/gml"))); // FIXME
				alService.getAlignment().setSchema1(schemaSource);


				
				
			// Target schema
			File fileTarget = new File(targetFilename);
			URI uriTarget = fileTarget.toURI();
		
			schemaService.loadSchema(uriTarget, SchemaType.TARGET);
		
			// update Alignment
			Schema schemaTarget = new Schema(schemaService.getTargetNameSpace(),
						new Formalism("GML 3.2.1 Application Schema", new URI(
								"http://www.opengis.net/gml"))); // FIXME
				alService.getAlignment().setSchema2(schemaTarget);
				
				
		} catch (Exception e2) {
			_log.error("Given Path/URL could not be parsed to an URI: ", e2);
		}
	}
	
	
	public void LoadInstanceData(String filename) {
		// get service references.
		final InstanceService instanceService = (InstanceService) PlatformUI.getWorkbench()
				.getService(InstanceService.class);
		final SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
		
		final String result = filename;
		
		final Display display = Display.getCurrent();
		

//					monitor.beginTask("Importing instance data...", IProgressMonitor.UNKNOWN);
					
					// retrieve required parameters, specifically the location and the namespace of the source schema.
					String namespace = schemaService.getSourceNameSpace();
					if (namespace == null) {
						// set a default namespace
						namespace = "http://xsdi.org/default";
					}
					URL schema_location = schemaService.getSourceURL();
					if (schema_location == null) {
						String message = "You have to load a Schema first.";
						throw new RuntimeException(message);
					}
					
					// retrieve and parse result from the Wizard.
					URL gml_location = null;
					try {
						File f = new File(result);
						gml_location = f.toURI().toURL();
					} catch (MalformedURLException e) {
						// it is ensured that only a valid URL is passed before
						throw new RuntimeException(result
								+ " was not parsed as a URL sucessfully: ", e);
					}
					
					// build FeatureCollection from the selected source.
					FeatureCollection<FeatureType, Feature> features = null;
					
					features = parseGML(namespace, schema_location, gml_location);
					
					final FeatureCollection<FeatureType, Feature> deployFeatures = features;
					
					if (features != null) {
						/* run in display thread because service update listeners 
						   might expect to be executed there */
						display.syncExec(new Runnable() {
							
							public void run() {
								instanceService.addInstances(DatasetType.reference, deployFeatures);
								_log.info(deployFeatures.size() + " instances were added to the InstanceService.");
							}
						});
					}
					
//					monitor.done();
	}
	
	private FeatureCollection<FeatureType, Feature> parseGML(
			String namespace, URL schema_location, URL gml_location) {
		
		FeatureCollection<FeatureType, Feature> result = null;
		try {
			Configuration configuration = new ApplicationSchemaConfiguration(
					namespace, schema_location.toExternalForm());
			
			configuration = new GMLConfiguration();

			_log.info("Using this GML location: " + gml_location.toString());
			
			URI file = new URI(URLDecoder.decode(gml_location.toString(), "UTF-8"));
			InputStream xml = new FileInputStream(new File(file));
			
			HaleGMLParser parser = new HaleGMLParser(configuration);
			result = 
				(FeatureCollection<FeatureType, Feature>) parser.parse(xml);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Parsing the given GML into a FeatureCollection failed: ",
					ex);
		}
		return result;
	}
	
	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		LoadSchemas(
				"D:/Humboldt/workspace/HALE2/resources/INSPIRE_Conf_Data/Watercourse/BY/SourceSchema/Watercourses_BY.xml",
				"D:/Humboldt/workspace/HALE2/resources/INSPIRE_Conf_Data/Watercourse/VA/SourceSchema/Watercourses_VA.xml");
		
//		LoadInstanceData("D:/Humboldt/workspace/HALE2/resources/INSPIRE_Conf_Data/Watercourse/BY/SourceData/GML/Watercourses_BY.gml");
		
		
		return null;
	}

}
