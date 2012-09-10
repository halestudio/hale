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

import java.util.Set;

import org.eclipse.jface.wizard.IWizardPage;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;

/**
 * Interface for a parameter configuration page of a function.
 * 
 * @author Kai Schwierczek
 */
public interface ParameterPage extends IWizardPage {

	/**
	 * Sets the parameters this page is responsible for and their initial
	 * values. <br>
	 * It should only handle the parameters in the given set, even if it could
	 * handle more.
	 * 
	 * @param params the parameters this page is responsible for
	 * @param initialValues initial values of those parameters, may be null,
	 *            should not be changed
	 */
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, String> initialValues);

	/**
	 * Returns the configuration of the parameters this page is responsible for. <br>
	 * It should only contain key value pairs, where key is the name of one of
	 * the parameters this page is responsible for.
	 * 
	 * @return the configuration of the parameters
	 */
	public ListMultimap<String, String> getConfiguration();
}
