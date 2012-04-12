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

package eu.esdihumboldt.hale.io.oml.helper;

/**
 * The interface for all translator functions
 * 
 * @author Kevin Mais
 */
public interface FunctionTranslator {

	/**
	 * Returns the new version of the transformation ID
	 * 
	 * @return the transformation ID
	 */
	public String getTransformationId();

}
