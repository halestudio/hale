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

package eu.esdihumboldt.hale.core.io.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.core.io.IOProvider;

/**
 * Abstract base class for implementing {@link IOProvider}s 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class AbstractIOProvider implements IOProvider {
	
	private final Map<String, String> parameters = new HashMap<String, String>();
	
	private final Set<String> supported = new HashSet<String>();

	/**
	 * Add a supported parameter name, should be called in the constructor
	 * 
	 * @param name the supported parameter name to add
	 */
	protected void addSupportedParameter(String name) {
		supported.add(name);
	}
	
	/**
	 * @see IOProvider#getParameter(String)
	 */
	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * @see IOProvider#getSupportedParameters()
	 */
	@Override
	public Set<String> getSupportedParameters() {
		return Collections.unmodifiableSet(supported);
	}

	/**
	 * @see IOProvider#setParameter(String, String)
	 */
	@Override
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}

}
