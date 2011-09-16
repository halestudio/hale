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

package eu.esdihumboldt.hale.common.align.extension.function;

import org.eclipse.core.runtime.IConfigurationElement;

import net.jcip.annotations.Immutable;

/**
 * Definition of a function parameter
 * @author Simon Templer
 */
@Immutable
public final class FunctionParameter extends AbstractParameter {
	
	//TODO restrictions on parameters?
	
	/**
	 * Create a function parameter definition
	 * @param conf the configuration element 
	 */
	public FunctionParameter(IConfigurationElement conf) {
		super(conf);
	}
	
}
