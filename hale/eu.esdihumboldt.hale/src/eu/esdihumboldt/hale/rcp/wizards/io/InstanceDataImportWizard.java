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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.Version;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.instanceprovider.InstanceConfiguration;
import eu.esdihumboldt.hale.instanceprovider.InstanceProvider;
import eu.esdihumboldt.hale.instanceprovider.gml.GmlInstanceProvider;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.provider.InstanceProviderFactory;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.ExceptionHelper;
import eu.esdihumboldt.hale.rcp.views.map.SelectCRSDialog;

/**
 * This {@link Wizard} controls the import of geodata to be used for 
 * visualisation of the transformations and for validation of their 
 * correctness.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @version $Id$
 */
public class InstanceDataImportWizard 
	extends Wizard implements IImportWizard {

	private static ALogger _log = ALoggerFactory.getLogger(InstanceDataImportWizard.class);

	private InstanceDataImportWizardMainPage mainPage;

	//private InstanceDataImportWizardFilterPage filterPage;
	//private InstanceDataImportWizardVerificationPage verificationPage;
	
	/**
	 * Default constructor
	 * 
	 * @param schemaNamespace the schema namespace
	 */
	public InstanceDataImportWizard(String schemaNamespace) {
		super();
		/*this.filterPage = new InstanceDataImportWizardFilterPage(
				Messages.FilterDataText,
				Messages.FilterImportGeodataText); // NON-NLS-1
		this.verificationPage = new InstanceDataImportWizardVerificationPage(
				Messages.InstanceDataImportTitle,
				Messages.InstanceDataImportDescription); // NON-NLS-1
		super.setWindowTitle(Messages.WindowTitle); // NON-NLS-1*/
		
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		String schemaFormat = ss.getSourceSchemaFormat();
		
		// determine available instance providers
		Collection<InstanceProvider> instanceProviders = Collections.unmodifiableCollection(InstanceProviderFactory.INSTANCE.getInstanceProvider(schemaFormat));
		
		setNeedsProgressMonitor(true);
		
		mainPage = new InstanceDataImportWizardMainPage(
				Messages.InstanceDataImportWizard_MainPageLabel, 
				Messages.ImportGeodataText,
				schemaNamespace, schemaFormat, instanceProviders); // NON-NLS-1
	}

	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return this.mainPage.isPageComplete();
	}

	/**
	 * Load instance data from source into {@link InstanceService}.
	 * 
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// get service references.
		final InstanceService instanceService = (InstanceService) PlatformUI.getWorkbench()
				.getService(InstanceService.class);
		final ProjectService projectService = (ProjectService) PlatformUI.getWorkbench()
				.getService(ProjectService.class);
		final URL result = mainPage.getResult();
		final ConfigurationType type = mainPage.getConfiguration();
		
		final Display display = Display.getCurrent();
		
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask(Messages.ImportDataStatusText, IProgressMonitor.UNKNOWN);
					
					try {
						final FeatureCollection<FeatureType, Feature> features = loadInstances(result.toURI(), type, null);
						
						if (features != null) {
							/* run in display thread because service update listeners 
							   might expect to be executed there */
							display.syncExec(new Runnable() {
								
								public void run() {
									instanceService.cleanInstances();
									SelectCRSDialog.resetCustomCRS();
									instanceService.addInstances(DatasetType.reference, features);
									_log.info(features.size() + " instances were added to the InstanceService."); //$NON-NLS-1$
								}
							});
						}
						
						projectService.setInstanceDataPath(result.toString());
						projectService.setInstanceDataType(type);
					} catch (Throwable e) {
						_log.userError("Error loading instance data", e);
					}
					
					monitor.done();
				}

			});
			
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				ExceptionHelper.handleException(
						e.getCause().getMessage(), 
						HALEActivator.PLUGIN_ID, e.getCause());
			}
			else {
				ExceptionHelper.handleException(
						e.getMessage(), 
						HALEActivator.PLUGIN_ID, e);
			}
			
			_log.error("Error performing wizard finish", e); //$NON-NLS-1$
		}
		System.gc();
		return true;
	}

	/**
	 * Load source instance data into a feature collection
	 * 
	 * @param location the data location
	 * @param type the GML configuration type FIXME GML specific
	 * @param ip the instance provider, if <code>null</code> it will be tried
	 *   to be auto-detected
	 * @return the feature collection
	 * @throws URISyntaxException if an invalid location is given
	 * @throws IOException if an error occurred reading the data
	 */
	public static FeatureCollection<FeatureType, Feature> loadInstances(URI location,
			ConfigurationType type, InstanceProvider ip) throws URISyntaxException, IOException {
		final SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		String schemaFormat = schemaService.getSourceSchemaFormat();
		
		// try to determine instance format
		String instanceFormat = FilenameUtils.getExtension(location.toString());
		if (instanceFormat != null && !instanceFormat.isEmpty()) {
			ip = InstanceProviderFactory.INSTANCE.getInstanceProvider(schemaFormat, instanceFormat);
		}
		
		if (ip == null) {
			// use default instance format
			ip = InstanceProviderFactory.INSTANCE.getInstanceProvider(schemaFormat, "gml");
		}
		
		Version instanceVersion;
		switch (type) {
		case GML3_2:
			instanceVersion = GmlInstanceProvider.GML32;
			break;
		case GML2:
			instanceVersion = new Version("2.0.0");
			break;
		case GML3:
		default:
			instanceVersion = GmlInstanceProvider.GML3;
		}
		
		InstanceConfiguration configuration = new InstanceConfiguration(
				schemaService.getSourceURL().toURI(), 
				schemaService.getSourceNameSpace(), 
				schemaService.getSourceSchemaElements(), 
				instanceVersion);
		
		return ip.loadInstances(location, configuration, null);
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// do nothing
	}

	/**
	 * @see IWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		super.addPage(this.mainPage);
//		super.addPage(this.filterPage);
//		super.addPage(this.verificationPage);
	}
	
//	private FeatureCollection<FeatureType, Feature> parseGML(URL gml_location, ConfigurationType type) {
//		
//		FeatureCollection<FeatureType, Feature> result = null;
//		try {
//			_log.info("Using this GML location: " + gml_location.toString()); //$NON-NLS-1$
//			
//			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
//			
//			result = loadGML(gml_location, type, ss);
//		} catch (Exception ex) {
//			throw new RuntimeException(
//					"Parsing the given GML into a FeatureCollection failed: " + ex.getMessage(), //$NON-NLS-1$
//					ex);
//		}
//		return result;
//	}

	/**
	 * Load a GML file from the given location
	 * 
	 * @param gmlLocation the location of the file
	 * @param type the configuration type
	 * @param ss the schema service
	 * 
	 * @return the feature collection with the loaded features
	 * @throws IOException if an error occurs reading the file
	 */
//	public static FeatureCollection<FeatureType, Feature> loadGML(URL gmlLocation,
//			ConfigurationType type, SchemaService ss) throws IOException {
//		ATransaction trans = _log.begin("Loading GML features from " + gmlLocation.toString());
//		try {
//			return GmlHelper.loadGml(gmlLocation.openStream(), type, 
//					ss.getSourceNameSpace(), ss.getSourceURL().toString(), ss.getSourceSchema());
//		} finally {
//			trans.end();
//		}
//	}
}
