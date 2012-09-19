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
package eu.esdihumboldt.hale.io.oml.internal.goml.omwg.comparator;

import java.util.List;

import org.apache.log4j.Logger;
import org.opengis.feature.Property;

import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Restriction;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueExpression;

/**
 * @author doylemr
 * 
 */
public class LessThanComparator implements IComparator {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(LessThanComparator.class);

	// TODO finish this method
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.esdihumboldt.goml.omwg.comparator.IComparator#evaluate(eu.esdihumboldt
	 * .goml.omwg.Restriction, org.opengis.feature.Property)
	 */
	@Override
	public boolean evaluate(Restriction sourceRestriction, Property sourceProp) {
		List<IValueExpression> sourceValues = sourceRestriction.getValue();
		Object sourcePropValue = sourceProp.getValue();

		LOG.debug("There are " + sourceValues.size() + " source values in the " + sourceRestriction);
		LOG.debug("The source property value is: " + sourcePropValue);

		// TODO Will there be a collection of source values for not equal?.
//		for (IValueExpression value : sourceValues) {
//		}
		LOG.debug("No matches found for " + sourcePropValue);
		return false;
	}

}
