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

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advisor for exporting source instances.
 * 
 * @author Simon Templer
 */
public class InstanceExportAdvisor extends DefaultIOAdvisor<InstanceWriter> {

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(InstanceWriter provider) {
		super.prepareProvider(provider);

		// set target schema
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
		provider.setTargetSchema(ss.getSchemas(SchemaSpaceID.TARGET));

		// set instances to export
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);
		provider.setInstances(is.getInstances(DataSet.TRANSFORMED));
	}

}
