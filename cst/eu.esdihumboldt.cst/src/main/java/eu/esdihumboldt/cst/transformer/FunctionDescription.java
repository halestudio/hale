/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer;

import java.net.URL;
import java.util.Map;

/**
 * An {@link FunctionDescription} provides essential information about a 
 * single transformation operation that a {@link CstService} offers.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface FunctionDescription {
	
	public URL getFunctionId();
	
	/**
	 * @return a Map of Parameter names (any strings allowed) and type names, 
	 * as strings. For type names, the following are allowed:
	 * - java code, including full package names, e.g. java.util.Map<java.lang.Object, java.lang.String>
	 * - XSD element definitions, such as xsd:string or xsd:date
	 * - primitive types, such as string, int, double
	 */
	public Map<String, Class<?>> getParameterConfiguration();

}
