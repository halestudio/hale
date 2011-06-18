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

import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.instance.io.InstanceReader;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Advisor for importing source instances
 * @author Simon Templer
 */
public class InstanceImportAdvisor extends AbstractIOAdvisor<InstanceReader> {

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(InstanceReader provider) {
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		provider.setSourceSchema(ss.getSchemas(SchemaSpaceID.SOURCE));
	}

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(InstanceReader provider) {
		// add instances to instance service
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		is.addSourceInstances(provider.getInstances());
	}

}
