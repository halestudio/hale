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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;

/**
 * Page for configuring function parameters.
 * @author Simon Templer
 */
public class ParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> 
		implements FunctionWizardPage {

	/**
	 * Default constructor
	 */
	public ParameterPage() {
		super("parameters");
		
		setTitle("Function parameters");
		setDescription("Specify the parameters for the relation");
		
		setPageComplete(false);
	}

	/**
	 * @see HaleWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		updateState();
	}

	/**
	 * Update the page state
	 */
	private void updateState() {
		//TODO
		setPageComplete(true);
	}

	/**
	 * @see FunctionWizardPage#configureCell(MutableCell)
	 */
	@Override
	public void configureCell(MutableCell cell) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		// TODO Auto-generated method stub
		
	}

}
