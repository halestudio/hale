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

package eu.esdihumboldt.hale.ui.io.action.wizard;

import org.eclipse.jface.wizard.IWizard;

import eu.esdihumboldt.hale.ui.util.handler.AbstractWizardHandler;

/**
 * Show the {@link SelectImportWizard}
 * 
 * @author Simon Templer
 */
public class SelectImportHandler extends AbstractWizardHandler {

	/**
	 * @see AbstractWizardHandler#createWizard()
	 */
	@Override
	protected IWizard createWizard() {
		return new SelectImportWizard();
	}

}
