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
 * ClassificationMapping function constants.
 * 
 * @author Kai Schwierczek
 */
public interface ClassificationMappingFunction {
	
	/**
	 * the classification mapping Id
	 */
	public static final String ID = "eu.esdihumboldt.hale.align.classification";
	
	
	/**
	 * Name of the parameter specifying the classifications.
	 */
	public static final String PARAMETER_CLASSIFICATIONS = "classificationMapping";

	/**
	 * Name of the parameter specifying what happens to unclassified values.
	 */
	public static final String PARAMETER_NOT_CLASSIFIED_ACTION = "notClassifiedAction";

	/**
	 * Value for PARAMETER_NOT_CLASSIFIED_ACTION specifying that the source value should be used.
	 */
	public static final String USE_SOURCE_ACTION = "source";

	/**
	 * Value for PARAMETER_NOT_CLASSIFIED_ACTION specifying that null should be used.
	 */
	public static final String USE_NULL_ACTION = "null";

	/**
	 * Value for PARAMETER_NOT_CLASSIFIED_ACTION specifying that a fixed value, which follows 
	 * the ":" should be used.
	 */
	public static final String USE_FIXED_VALUE_ACTION_PREFIX = "fixed:";
}
