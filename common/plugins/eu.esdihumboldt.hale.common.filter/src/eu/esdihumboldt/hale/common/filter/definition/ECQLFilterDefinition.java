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

package eu.esdihumboldt.hale.common.filter.definition;

import org.geotools.filter.text.cql2.CQLException;

import eu.esdihumboldt.hale.common.filter.FilterGeoECqlImpl;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinition;
import eu.esdihumboldt.util.definition.ObjectDefinition;

/**
 * Object definition for ECQL filters.
 * 
 * @author Simon Templer
 */
public class ECQLFilterDefinition implements FilterDefinition<FilterGeoECqlImpl> {

	/**
	 * The filter definition ID.
	 */
	public static final String ID = "ECQL";

	/**
	 * @see ObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return ID;
	}

	/**
	 * @see ObjectDefinition#getObjectClass()
	 */
	@Override
	public Class<FilterGeoECqlImpl> getObjectClass() {
		return FilterGeoECqlImpl.class;
	}

	/**
	 * @see ObjectDefinition#parse(String)
	 */
	@Override
	public FilterGeoECqlImpl parse(String value) {
		try {
			return new FilterGeoECqlImpl(value);
		} catch (CQLException e) {
			throw new IllegalArgumentException("Could not parse ECQL expression", e);
		}
	}

	/**
	 * @see ObjectDefinition#asString(Object)
	 */
	@Override
	public String asString(FilterGeoECqlImpl object) {
		return object.getFilterTerm();
	}

}
