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

import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.ui.io.ImportWizard;

/**
 * Wizard for importing instances
 * 
 * @author Simon Templer
 * @since 2.5
 */
public class InstanceImportWizard extends ImportWizard<InstanceReader> {

	/**
	 * Create an instance import wizard
	 */
	public InstanceImportWizard() {
		super(InstanceReader.class);

		setWindowTitle("Import instances");
	}

}
