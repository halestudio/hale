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

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.apache.log4j.Logger;
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
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.gmlparser.HaleGMLParser;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
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

	private static Logger _log = Logger.getLogger(InstanceDataImportWizard.class);

	InstanceDataImportWizardMainPage mainPage;
	InstanceDataImportWizardFilterPage filterPage;
	InstanceDataImportWizardVerificationPage verificationPage;

	/**
	 * Default constructor
	 */
	public InstanceDataImportWizard() {
		super();
		this.mainPage = new InstanceDataImportWizardMainPage(
				Messages.InstanceDataImportWizard_MainPageLabel, Messages.ImportGeodataText); // NON-NLS-1
		this.filterPage = new InstanceDataImportWizardFilterPage(
				Messages.FilterDataText,
				Messages.FilterImportGeodataText); // NON-NLS-1
		this.verificationPage = new InstanceDataImportWizardVerificationPage(
				Messages.InstanceDataImportTitle,
				Messages.InstanceDataImportDescription); // NON-NLS-1
		super.setWindowTitle(Messages.WindowTitle); // NON-NLS-1
		super.setNeedsProgressMonitor(true);
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
	public boolean performFinish() {
		// get service references.
		final InstanceService instanceService = (InstanceService) PlatformUI.getWorkbench()
				.getService(InstanceService.class);
		final ProjectService projectService = (ProjectService) PlatformUI.getWorkbench()
				.getService(ProjectService.class);
		
		final URL result = mainPage.getResult();
		
		final Display display = Display.getCurrent();
		
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask(Messages.ImportDataStatusText, IProgressMonitor.UNKNOWN);
					
					// build FeatureCollection from the selected source.
					FeatureCollection<FeatureType, Feature> features = parseGML(result);
					
					final FeatureCollection<FeatureType, Feature> deployFeatures = features;
					
					if (features != null) {
						/* run in display thread because service update listeners 
						   might expect to be executed there */
						display.syncExec(new Runnable() {
							
							public void run() {
								instanceService.cleanInstances();
								SelectCRSDialog.resetCustomCRS();
								instanceService.addInstances(DatasetType.reference, deployFeatures);
								_log.info(deployFeatures.size() + " instances were added to the InstanceService."); //$NON-NLS-1$
							}
						});
					}
					
					monitor.done();
					projectService.setInstanceDataPath(result.toString());
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
		
		return true;
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
	public void addPages() {
		super.addPages();
		super.addPage(this.mainPage);
//		super.addPage(this.filterPage);
//		super.addPage(this.verificationPage);
	}
	
	@SuppressWarnings("unchecked")
	private FeatureCollection<FeatureType, Feature> parseGML(URL gml_location) {
		
		FeatureCollection<FeatureType, Feature> result = null;
		try {
			Configuration configuration = new GMLConfiguration();

			_log.info("Using this GML location: " + gml_location.toString()); //$NON-NLS-1$
			
			HaleGMLParser parser = new HaleGMLParser(configuration);
			result = (FeatureCollection<FeatureType, Feature>) parser.parse(gml_location.openStream());
		} catch (Exception ex) {
			throw new RuntimeException(
					"Parsing the given GML into a FeatureCollection failed: " + ex.getMessage(), //$NON-NLS-1$
					ex);
		}
		return result;
	}
}
