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
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.gmlhandler.GmlHandler;
import eu.esdihumboldt.gmlhandler.gt2deegree.TypeIndex;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SaveTransformationResultHandler extends AbstractHandler {
	
	private static final ALogger log = ALoggerFactory.getLogger(SaveTransformationResultHandler.class);

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		final FeatureCollection<FeatureType, Feature> features = is.getFeatures(DatasetType.transformed);
		
		if (features == null || features.isEmpty()) {
			log.userError("No transformed features are available for export.");
			return null;
		}
		
		final URL targetSchema = ss.getTargetURL();
		final String targetNamespace = ss.getTargetNameSpace();
		final Map<String, String> prefixes = ss.getTargetPrefixes();
		
		// determine output file
		FileDialog files = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		
		String[] extensions = new String[2]; 
		extensions[0]= "*.gml"; //$NON-NLS-1$
		extensions[1]= "*.xml"; //$NON-NLS-1$
		files.setFilterExtensions(extensions);
		
		String filename = files.open();
		final File file = new File(filename);
		
		final TypeIndex types = new TypeIndex();
		for (SchemaElement se : ss.getTargetSchema()) {
			types.addType(se.getType());
		}
		
		Display display = PlatformUI.getWorkbench().getDisplay();
		try {
			IRunnableWithProgress op = new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("Exporting transformed features to GML file", IProgressMonitor.UNKNOWN);
					GmlHandler handler = GmlHandler.getDefaultInstance(targetSchema.toString(), file.getAbsolutePath()); //(new URL(outputFilename)).getFile());	
					try {
						handler.writeFC(features, types, targetNamespace, prefixes);
					} catch (Exception e) {
						log.userError("Error saving transformation result to GML file", e);
					} finally {
						monitor.done();
					}
				}
			};
		    new ProgressMonitorDialog(display.getActiveShell()).run(true, false, op);
		} catch (Exception e1) {
			log.userError("Error saving transformation result to GML file", e1);
		}
	
		return null;
	}

}
