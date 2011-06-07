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

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.instance.io.InstanceReader;
import eu.esdihumboldt.hale.instance.io.InstanceReaderFactory;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.ImportWizard;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Wizard for importing instances
 * @author Simon Templer
 */
public class InstanceImportWizard extends ImportWizard<InstanceReader, InstanceReaderFactory> {

	/**
	 * Create an instance import wizard
	 */
	public InstanceImportWizard() {
		super(InstanceReaderFactory.class);
	}

	/**
	 * @see IOWizard#updateConfiguration(IOProvider)
	 */
	@Override
	protected void updateConfiguration(InstanceReader provider) {
		super.updateConfiguration(provider);
		
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		provider.setSourceSchema(ss.getSchemas(SchemaSpaceID.SOURCE));
	}

	/**
	 * @see IOWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean success = super.performFinish();
		
		if (success) {
			// add instances to instance service
			InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
			is.addSourceInstances(getProvider().getInstances());
		}
		
		return success;
	}

}
