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

import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl;
import eu.esdihumboldt.hale.common.instance.extension.FilterDefinition;
import eu.esdihumboldt.util.definition.ObjectDefinition;

/**
 * Object definition for CQL filters. 
 * @author Simon Templer
 */
public class CQLFilterDefinition implements FilterDefinition<FilterGeoCqlImpl> {

	/**
	 * @see ObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return "CQL";
	}

	/**
	 * @see ObjectDefinition#getObjectClass()
	 */
	@Override
	public Class<FilterGeoCqlImpl> getObjectClass() {
		return FilterGeoCqlImpl.class;
	}

	/**
	 * @see ObjectDefinition#parse(String)
	 */
	@Override
	public FilterGeoCqlImpl parse(String value) {
		try {
			return new FilterGeoCqlImpl(value);
		} catch (CQLException e) {
			throw new IllegalArgumentException("Could not parse CQL expression", e);
		}
	}

	/**
	 * @see ObjectDefinition#asString(Object)
	 */
	@Override
	public String asString(FilterGeoCqlImpl object) {
		return object.getFilterTerm();
	}

}
