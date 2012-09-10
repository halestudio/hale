/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.dataaccess;

import eu.esdihumboldt.specification.dataaccess.exceptions.AccessException;

/**
 * This interface represents the boundary between the Request broker pipe and
 * concrete upstream data sources.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface DataAccessService {

	/**
	 * This operation executes an {@link AccessRequest}.
	 * 
	 * @param request
	 *            the AccessRequest to be executed by the
	 *            {@link DataAccessService}.
	 * @return the AccessResponse returned by the created AccessCartridge.
	 * @throws AccessException
	 *             if the request has irrevocably failed
	 */
	public AccessResponse runRequest(AccessRequest request)
			throws AccessException;

}
