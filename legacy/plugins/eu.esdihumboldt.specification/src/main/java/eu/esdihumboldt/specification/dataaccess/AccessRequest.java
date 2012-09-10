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
package eu.esdihumboldt.specification.dataaccess;

import java.util.Map;

import eu.esdihumboldt.specification.dataaccess.abstractionmodel.AbstractionModel;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.context.Context;

/**
 * An AccessRequest encapsulates the query created by the RequestBroker towards
 * an instance of an AccessCartridge. It is essentially made up of a set of
 * attributes that can be seen as query parameters and of a
 * HarmonisationContext, which contains information on the client requesting the
 * access. This can become important for authorization and authentication with
 * remote services and is also necessary to identify responses in asynchronous
 * communication.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AccessRequest {

	/**
	 * @return A Map containing the Constraints important for this request.
	 */
	public Map<String, Constraint> getConstraints();

	/**
	 * @return The {@link Context} for this {@link AccessRequest}.
	 */
	public Context getContext();

	/**
	 * @return If this request represents a save/update operation, this method
	 *         returns the actual data to be saved or updated.
	 */
	public AbstractionModel getAttachedData();

}
