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

package eu.esdihumboldt.hale.schemaprovider;

import java.util.Set;

import org.opengis.feature.type.AttributeType;

/**
 * Enumeration attribute type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public interface EnumAttributeType extends AttributeType {

	/**
	 * Get the allowed values
	 * 
	 * @return the allowed values
	 */
	public Set<String> getAllowedValues();

	/**
	 * Determines if other values than the values returned by
	 * {@link #getAllowedValues()} are allowed
	 * 
	 * @return true if other values are allowed, false otherwise
	 */
	public boolean otherValuesAllowed();

}
