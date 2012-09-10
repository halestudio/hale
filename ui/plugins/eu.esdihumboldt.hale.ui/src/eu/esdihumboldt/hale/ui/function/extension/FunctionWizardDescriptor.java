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

package eu.esdihumboldt.hale.ui.function.extension;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;

/**
 * Function wizard descriptor
 * 
 * @param <T> the type of the function definition
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface FunctionWizardDescriptor<T extends AbstractFunction<?>> extends
		ExtensionObjectFactory<FunctionWizardFactory>, FunctionWizardFactory {

	/**
	 * Get the ID of the associated function
	 * 
	 * @return the function ID
	 */
	public String getFunctionId();

	/**
	 * Get the function definition
	 * 
	 * @return the function definition
	 */
	public T getFunction();

}