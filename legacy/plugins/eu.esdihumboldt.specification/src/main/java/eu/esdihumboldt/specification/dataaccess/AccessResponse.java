/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.dataaccess;

import java.util.UUID;

import eu.esdihumboldt.specification.dataaccess.abstractionmodel.AbstractionModel;
import eu.esdihumboldt.specification.mediator.ResponseStatus;

/**
 * The AccessResponse is a container or transfer type that is used to
 * encapsulate various types of response that an AccessCartridge can provide. It
 * basically contains status information on how the request was executed and for
 * the data actually returned as result of the operation.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AccessResponse {

	/**
	 * @return a long UID unique to the current VM that identifies a request-
	 *         response doublet and is used for direction of asynchronous calls.
	 */
	public UUID getUID();

	/**
	 * @return the Status type that this AccessResponse has.
	 */
	public ResponseStatus getStatus();

	/**
	 * @return an AbstractionModel filled with the actual results of the query
	 *         directed towards a AccessCartridge. When loading data from a
	 *         service, this will contain a representation of that service's
	 *         answer, harmonised to a metastructure.
	 */
	public AbstractionModel getResult();

	// ------------------------------------------------------------------------//

}
