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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.rcp.views.map.SelectCRSDialog;
import eu.esdihumboldt.hale.rcp.wizards.io.gml.GmlExportWizard;
import eu.esdihumboldt.hale.schemaprovider.Schema;

/**
 * Save the transformation result to a GML file
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SaveTransformationResultHandler extends AbstractHandler {
	
	private static final ALogger log = ALoggerFactory.getLogger(SaveTransformationResultHandler.class);

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		final FeatureCollection<FeatureType, Feature> features = is.getFeatures(DatasetType.transformed);
		
		if (features == null || features.isEmpty()) {
			log.userError(Messages.SaveTransformationResultHandler_0); 
			return null;
		}
		
		// display Report Viewer
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("eu.esdihumboldt.hale.rcp.views.reportView"); //$NON-NLS-1$
		} catch (PartInitException e) {
			log.error("Could not open reportView", e); //$NON-NLS-1$
		}
		
		final Schema targetSchema = ss.getTargetSchema();
		
		// determine SRS
		final String commonSrsName = SelectCRSDialog.getValue().getIdentifiers().iterator().next().toString();
		
		GmlExportWizard wizard = new GmlExportWizard(features, targetSchema, commonSrsName); 
		Shell shell = HandlerUtil.getActiveShell(event);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();
	
		return null;
	}

}
