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
package eu.esdihumboldt.hale.ui.function;

import org.eclipse.jface.wizard.IWizard;

import eu.esdihumboldt.hale.common.align.model.MutableCell;

/**
 * Function wizard interface
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface FunctionWizard extends IWizard {

	/**
	 * Initialize the wizard. Is called after wizard construction.
	 */
	public void init();

	/**
	 * Get the result cell
	 * 
	 * @return the result cell
	 */
	public MutableCell getResult();

}
