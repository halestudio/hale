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
package eu.esdihumboldt.specification.mediator;

import java.util.Map;
import java.util.SortedSet;
import java.util.UUID;

import eu.esdihumboldt.specification.dataaccess.AccessResponse;
import eu.esdihumboldt.specification.mediator.exceptions.IncompleteResponseException;

/**
 * The AccessQueueManager (AQM) accepts the individual AccessRequests created by
 * the RequestBroker or by the TransformationQueueManager. It performs the
 * following duties:
 * <ul>
 * <li>Creation of a AccessCartridgeProfile from the MediatorComplexRequest by
 * use of the AccessCartridgeProfiler</li>
 * <li>Delegation of AccessRequests to the DACF.</li>
 * <li>Notification of the RequestBroker when an AccessRequest was completed and
 * the coresponding AccessResponse is available.</li>
 * </ul>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AccessQueueManager {

	/**
	 * Enqueues one MediatorComplexRequest for asynchronous execution.
	 * 
	 * @param request
	 *            the HarmonisationRequest for which data needs to be retrieved.
	 * @return the UUID identifying this request/response pair. It is generally
	 *         recommended to use the request UUID as well to make
	 *         implementation easier.
	 */
	public UUID enqueueRequest(MediatorComplexRequest request);

	/**
	 * Enqueues a SortedSet of HarmonizationRequests for asynchronous execution.
	 * 
	 * @param requests
	 *            the SortedSet of HarmonisationRequests for which data needs to
	 *            be retrieved. The requests will be processed in the natural
	 *            order of the Set, i.e. by the values returned by the
	 *            HarmonisationRequest's compareTo method.
	 * @return the UUID identifying this request/response pair. It is generally
	 *         recommended to use the request UUID as well to make
	 *         implementation easier.
	 */
	public Map<MediatorComplexRequest, UUID> enqueueRequests(
			SortedSet<MediatorComplexRequest> requests);

	/**
	 * Simpler API for cases where just one Request/response has to be managed.
	 * This operation leads to synchronous execution of the Request.
	 * 
	 * @param request
	 *            the MediatorComplexRequest for which a data access should be
	 *            executed.
	 * @return an AccessResponse for the Request.
	 * @throws IncompleteResponseException
	 *             if the MediatorComplexRequest failed.
	 */
	public AccessResponse createResponse(MediatorComplexRequest request)
			throws IncompleteResponseException;

}
