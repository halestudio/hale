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

import eu.esdihumboldt.specification.dataaccess.AccessResponse;

/**
 * This is the main workflow controller of the Mediator. It gets the
 * HarmoizationRequest created by the specific InterfaceController instance that
 * was invoked by a client and then manages the necessary steps to fulfill this
 * request, including the semantic transformation of the request, the decision
 * which data sources to access (actual access is managed by the DACF) and the
 * transformation of the abstracted part responses to a harmonized and unified
 * response. This response is then provided back to the InterfaceController
 * instance. <br/>
 * <br/>
 * To decide which data sources to access and which transformation workflow has
 * to be applied, the Constraints play the central role. These are being used as
 * query elements for the Information Grounding Service to select data sources,
 * and are also directly linked to processing and transformation steps.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface RequestBroker {

	/**
	 * @param request
	 *            the MediatorComplexRequest created from the client request and
	 *            the ContextService context.
	 * @return the completed MediatorResponse to be encoded and sent by the
	 *         InterfaceController.
	 */
	public MediatorResponse transformAndExecute(MediatorComplexRequest request);

	/**
	 * This operation is used by the AccessQueueManager to tell the
	 * RequestBroker that a certain Request has been completed and that the
	 * AccessResponse is complete. The RB will then usually fowrad the
	 * AccessResponse to the TransformationQueue, depending on the workfow that
	 * has been set up.
	 * 
	 * @param ares
	 *            the completed AccessResponse.
	 */
	public void notify(AccessResponse ares);

	/**
	 * This operation is used by the TransformationQueueManager to notify the
	 * RequestBroker that a transformation/workflow was completed (i.e. reached
	 * its terminal state) and passes the result to the
	 * TransformationQueueManager.
	 * 
	 * @param hres
	 *            the MediatorResponse that contains the end result of the
	 *            Transformation.
	 */
	public void notify(MediatorResponse hres);

}
