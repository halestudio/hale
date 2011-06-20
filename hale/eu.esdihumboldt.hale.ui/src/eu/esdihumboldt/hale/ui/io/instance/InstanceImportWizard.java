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

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.instance.io.InstanceReader;
import eu.esdihumboldt.hale.instance.io.InstanceReaderFactory;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.ImportWizard;
import eu.esdihumboldt.hale.ui.io.instance.crs.DialogCRSProvider;

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
		
		setWindowTitle("Import instances");
	}

	/**
	 * @see IOWizard#updateConfiguration(IOProvider)
	 */
	@Override
	protected void updateConfiguration(InstanceReader provider) {
		super.updateConfiguration(provider);
		
		provider.setDefaultCRSProvider(new DialogCRSProvider());
	}

}
