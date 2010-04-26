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

package eu.esdihumboldt.cst.transformer.service.impl;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * This enumeration is used to describe both the cardinality a given Cell 
 * describes on the level of {@link FeatureType}s and of the {@link Feature}
 * instances.
 *
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public enum CellCardinalityType {
	/** one to one */
	one_to_one,
	/** one to many */
	one_to_many,
	/** many to one */
	many_to_one,
	/** many to many */
	many_to_many
}