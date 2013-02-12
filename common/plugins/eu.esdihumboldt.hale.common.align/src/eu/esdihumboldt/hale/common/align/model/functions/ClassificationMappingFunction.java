/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model.functions;

/**
 * ClassificationMapping function constants.
 * 
 * @author Kai Schwierczek, Dominik Reuter
 */
public interface ClassificationMappingFunction {

	/**
	 * Name of the parameter specifying the lookupTable.
	 */
	public static final String PARAMETER_LOOKUPTABLE_ID = "lookupTableID";

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
	 * Value for PARAMETER_NOT_CLASSIFIED_ACTION specifying that the source
	 * value should be used.
	 */
	public static final String USE_SOURCE_ACTION = "source";

	/**
	 * Value for PARAMETER_NOT_CLASSIFIED_ACTION specifying that null should be
	 * used.
	 */
	public static final String USE_NULL_ACTION = "null";

	/**
	 * Value for PARAMETER_NOT_CLASSIFIED_ACTION specifying that a fixed value,
	 * which follows the ":" should be used.
	 */
	public static final String USE_FIXED_VALUE_ACTION_PREFIX = "fixed:";
}
