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
package eu.esdihumboldt.specification.mediator.constraints;

import java.util.Map;

/**
 * The {@link ProtocolConstraint} is not a {@link Constraint} in the strict
 * sense, but instead contains useful information derived from the protocol
 * layer of a request that was sent to the Mediator. In the example of a HTTP
 * request, this will include things like the method used, the authentication
 * scheme, the URL that was requested, cookies and other information.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ProtocolConstraint extends Constraint {

	/**
	 * @return a Map, with the parameter names (see protocol specification)
	 *         serving as Keys and the parameter values as values.
	 */
	public Map<String, String> getProtocolParameters();

	/**
	 * @param _key
	 *            the key String for which to retrieve the value.
	 * @return the value for the given key, or null if there is no correspondent
	 *         value.
	 */
	public String getParameter(String _key);

}
