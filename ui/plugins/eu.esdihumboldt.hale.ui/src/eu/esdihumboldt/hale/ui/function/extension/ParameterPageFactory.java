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

package eu.esdihumboldt.hale.ui.function.extension;

import java.util.Set;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Factory for parameter pages.
 * 
 * @author Kai Schwierczek
 */
public interface ParameterPageFactory extends ExtensionObjectFactory<ParameterPage> {

	/**
	 * Get the ID of the associated function.
	 * 
	 * @return the function ID
	 */
	public String getFunctionId();

	/**
	 * Get the associated parameters.
	 * 
	 * @return the associated parameters
	 */
	public Set<FunctionParameter> getAssociatedParameters();
}
