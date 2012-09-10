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

package eu.esdihumboldt.hale.ui.function.generic;

import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;

/**
 * Factory for generic function wizards
 * 
 * @author Simon Templer
 */
public abstract class AbstractGenericFunctionWizardFactory implements FunctionWizardFactory {

	private final String functionId;

	/**
	 * Create a generic function wizard factory for the function with the given
	 * identifier.
	 * 
	 * @param functionId the function identifier
	 */
	public AbstractGenericFunctionWizardFactory(String functionId) {
		super();
		this.functionId = functionId;
	}

	/**
	 * Get the function identifier
	 * 
	 * @return the function identifier
	 */
	public String getFunctionId() {
		return functionId;
	}

}
