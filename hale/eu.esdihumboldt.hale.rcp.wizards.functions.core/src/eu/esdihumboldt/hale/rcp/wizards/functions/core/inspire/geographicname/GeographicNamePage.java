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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire.geographicname;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;

/**
 * The WizardPage for the {@link GeographicNameFunctionWizard}
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class GeographicNamePage extends AbstractSingleCellWizardPage {

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public GeographicNamePage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		
	}

	/**
	 * @param pageName
	 */
	public GeographicNamePage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub

	}

}
