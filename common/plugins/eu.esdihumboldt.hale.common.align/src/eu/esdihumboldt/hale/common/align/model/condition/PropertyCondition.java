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

package eu.esdihumboldt.hale.common.align.model.condition;

import eu.esdihumboldt.hale.common.align.model.Property;

/**
 * Condition a {@link Property} may fulfill. Implementations may not hold any
 * state apart from its configuration.
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface PropertyCondition extends EntityCondition<Property> {

	// concrete typed interface

	// TODO some possibility to process property values?
	// e.g. for conversion - or should this be the responsibility of the
	// function implementation?

}
