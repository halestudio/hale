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

package eu.esdihumboldt.hale.common.align.model.functions;

/**
 * Merge function constants.
 * @author Simon Templer
 */
public interface MergeFunction {

	/**
	 * Name of the parameter specifying a property path (key property)
	 */
	public static final String PARAMETER_PROPERTY = "property";

	/**
	 * Name of the parameter specifying a property path (no key property)
	 */
	public static final String PARAMETER_ADDITIONAL_PROPERTY = "additional_property";

	/**
	 * Name of the parameter specifying whether auto detection of other equal properties
	 */
	public static final String PARAMETER_AUTO_DETECT = "auto_detect";
	
	/**
	 * the merge function Id
	 */
	public static final String ID = "eu.esdihumboldt.hale.align.merge";

}
