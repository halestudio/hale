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

package eu.esdihumboldt.hale.rcp.views.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.geotools.styling.Style;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.rcp.wizards.io.InstanceDataImportWizard;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.DefaultGeometries;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.IDefaultGeometries;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Action that sets a default geometry property for a type 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SetAsDefaultGeometryAction extends Action {
	
	private static final ALogger log = ALoggerFactory.getLogger(SetAsDefaultGeometryAction.class);

	/**
	 * Reload source schema and instances
	 */
	public static class ReloadSourceRunner implements IRunnableWithProgress {

		/**
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(final IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor.beginTask(Messages.SetAsDefaultGeometryAction_0, IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			ATransaction trans = log.begin("Reloading source schema and data"); //$NON-NLS-1$
			try {
				// reload source schema and instance data
				InstanceService instanceService = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
				ProjectService projectService = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
				SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
				StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
				
				// remember style
				Style style = styleService.getStyle();
				
				// remember instance data info
				ConfigurationType conf = projectService.getInstanceDataType();
				String instanceLoc = projectService.getInstanceDataPath();
				
				// clean instance data
				instanceService.cleanInstances();
	
				// remember source schema info
				String schemaLoc = projectService.getSourceSchemaPath();
				
				// clean source schema
				schemaService.cleanSourceSchema();
				
				// reload schema
				try {
					schemaService.loadSchema(new URI(schemaLoc), (String)null, SchemaType.SOURCE, new ProgressIndicator() {
						
						@Override
						public void begin(String taskName, int totalWork) {
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
							// do nothing
						}
						
						@Override
						public void setCurrentTask(String taskName) {
							monitor.subTask(taskName);
						}
					});
				} catch (Exception e) {
					log.userError(Messages.SetAsDefaultGeometryAction_4, e); 
				}
				
				styleService.addStyles(style);
				
				// readd instances
				monitor.subTask(Messages.SetAsDefaultGeometryAction_3); //$NON-NLS-1$
				if (instanceLoc != null && !instanceLoc.isEmpty()) {
					try {
						instanceService.addInstances(DatasetType.reference, 
								InstanceDataImportWizard.loadInstances(new URI(instanceLoc), conf, null));
					} catch (Exception e) {
						log.userError(Messages.SetAsDefaultGeometryAction_6, e); 
					}
				}
			} finally {
				trans.end();
				monitor.done();
			}
		}

	}
	
	/**
	 * Reload target schema and transformed instances
	 */
	public static class ReloadTargetRunner implements IRunnableWithProgress {

		/**
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(final IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor.beginTask(Messages.SetAsDefaultGeometryAction_5, IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			ATransaction trans = log.begin("Reloading target schema and data"); //$NON-NLS-1$
			try {
				// reload source schema and instance data
				InstanceService instanceService = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
				ProjectService projectService = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
				SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
				StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
				
				// remember style
				Style style = styleService.getStyle();

				// remember instance data
				FeatureCollection<FeatureType, Feature> instances = instanceService.getFeatures(DatasetType.reference);
				
				// clean transformed data
				instanceService.cleanInstances();
	
				// remember target schema info
				String schemaLoc = projectService.getTargetSchemaPath();
				
				// clean target schema
				schemaService.cleanTargetSchema();
				
				// reload schema
				try {
					schemaService.loadSchema(new URI(schemaLoc), (String)null, SchemaType.TARGET, new ProgressIndicator() {
						
						@Override
						public void begin(String taskName, int totalWork) {
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
							// do nothing
						}
						
						@Override
						public void setCurrentTask(String taskName) {
							monitor.subTask(taskName);
						}
					});
				} catch (Exception e) {
					log.userError(Messages.SetAsDefaultGeometryAction_7, e); 
				}
				
				styleService.addStyles(style);
				
				// trigger retransformation
				monitor.subTask(Messages.SetAsDefaultGeometryAction_8); //$NON-NLS-1$
				// use a trick to trigger the transformation - add empty feature collection
				instanceService.addInstances(DatasetType.reference, instances);
			} finally {
				trans.end();
				monitor.done();
			}
		}

	}

	private final SchemaItem item;
	
	/**
	 * Constructor
	 * 
	 * @param item the geometry property schema item
	 */
	public SetAsDefaultGeometryAction(SchemaItem item) {
		this.item = item;
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		SchemaItem parent = item.getParent(); // parent must be a feature type
		
		String propertyName = item.getName().getLocalPart();
		Definition def = parent.getDefinition();
		Name typeName = (def instanceof TypeDefinition)?(((TypeDefinition) def).getName()):(((SchemaElement) def).getTypeName());
		
		IRunnableWithProgress runner = null;
		Display display = Display.getCurrent();
		
		switch (item.getSchemaType()) {
		case SOURCE:
			// reload schema and instances
			if (!MessageDialog.openQuestion(display.getActiveShell(), Messages.SetAsDefaultGeometryAction_9,  //$NON-NLS-1$
					Messages.SetAsDefaultGeometryAction_1)) { //$NON-NLS-1$
				return;
			}
			runner = new ReloadSourceRunner();
			break;
		case TARGET:
			// reload target schema and transformations
			if (!MessageDialog.openQuestion(display.getActiveShell(), Messages.SetAsDefaultGeometryAction_12,  //$NON-NLS-1$
					Messages.SetAsDefaultGeometryAction_2)) { //$NON-NLS-1$
				return;
			}
			runner = new ReloadTargetRunner();
			break;
		}
		
		IDefaultGeometries defaultGeometries;
		try {
			Class<?> dfImpl = Class.forName("eu.esdihumboldt.hale.schemaprovider.uiconfig.DefaultGeometries",
					true, SchemaProvider.class.getClassLoader());
			defaultGeometries = (IDefaultGeometries) dfImpl.newInstance();
		} catch (Exception e) {
			defaultGeometries = DefaultGeometries.getInstance();
		}
		defaultGeometries.setDefaultGeometryName(typeName, propertyName);
		
		if (runner != null) {
			try {
				new ProgressMonitorDialog(display.getActiveShell()).run(true, false, runner);
			} catch (Exception e) {
				// ignore
				log.error("Error starting reload process", e); //$NON-NLS-1$
			}
		}
	}

}
