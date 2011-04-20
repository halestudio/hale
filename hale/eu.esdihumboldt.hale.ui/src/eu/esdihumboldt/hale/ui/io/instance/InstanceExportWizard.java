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

package eu.esdihumboldt.hale.ui.io.instance;

import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.instance.io.InstanceWriterFactory;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.rcp.views.map.SelectCRSDialog;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Wizard for exporting instances
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class InstanceExportWizard extends ExportWizard<InstanceWriter, InstanceWriterFactory> {

	/**
	 * Default constructor
	 */
	public InstanceExportWizard() {
		super(InstanceWriterFactory.class);
		
		setWindowTitle("Export instances");
	}

	/**
	 * @see ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		//TODO add configuration pages?!!
	}

	/**
	 * @see IOWizard#updateConfiguration(IOProvider)
	 */
	@Override
	protected void updateConfiguration(InstanceWriter provider) {
		super.updateConfiguration(provider);
		
		// configure with instances, common SRS, target schema
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		FeatureCollection<FeatureType, Feature> features = is.getFeatures(DatasetType.transformed);
		Schema targetSchema = ss.getTargetSchema();
		
		// determine SRS
		String commonSRSName;
		try {
			commonSRSName = SelectCRSDialog.getValue().getIdentifiers().iterator().next().toString();
		} catch (Exception e) {
			// ignore
			commonSRSName = null;
		}
		
		provider.setInstances(features);
		provider.setTargetSchema(targetSchema);
		provider.setCommonSRSName(commonSRSName);
	}

}
