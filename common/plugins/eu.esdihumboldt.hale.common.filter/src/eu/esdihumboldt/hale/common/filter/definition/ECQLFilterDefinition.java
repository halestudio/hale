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

package eu.esdihumboldt.hale.common.filter.definition;

import org.geotools.filter.text.cql2.CQLException;

import eu.esdihumboldt.hale.common.filter.FilterGeoECqlImpl;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinition;
import eu.esdihumboldt.util.definition.ObjectDefinition;

/**
 * Object definition for ECQL filters. 
 * @author Simon Templer
 */
public class ECQLFilterDefinition implements FilterDefinition<FilterGeoECqlImpl> {

	/**
	 * @see ObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return "ECQL";
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
