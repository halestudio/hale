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

package eu.esdihumboldt.hale.ui.transformation;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Export advisor for the transform data export using a specified instance collection.
 *
 * @author Kai Schwierczek
 */
public class TransformDataExportAdvisor extends DefaultIOAdvisor<InstanceWriter>{
	private final InstanceCollection instances;

	/**
	 * Default constructor.
	 *
	 * @param instances the instance collection to use for the export.
	 */
	public TransformDataExportAdvisor(InstanceCollection instances) {
		this.instances = instances;
	}

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(InstanceWriter provider) {
		super.prepareProvider(provider);
		
		// set target schema
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		provider.setTargetSchema(ss.getSchemas(SchemaSpaceID.TARGET));
		
		// set instances to export
		provider.setInstances(instances);
	}
}
