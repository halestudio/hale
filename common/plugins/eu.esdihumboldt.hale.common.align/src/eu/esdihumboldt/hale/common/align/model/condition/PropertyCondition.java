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
