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

import java.util.UUID;

import eu.esdihumboldt.specification.dataaccess.AccessResponse;
import eu.esdihumboldt.specification.mediator.exceptions.IncompleteResponseException;
import eu.esdihumboldt.specification.mediator.exceptions.UnknownProcessException;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;
import eu.esdihumboldt.specification.workflow.repository.Transformer;

/**
 * The TransformationQueueManager (TQM) is used by the RequestBroker to apply
 * processing to various types of data at various stages. It is used both on the
 * request path and on the response path, whenever a conceptual schema
 * translation, geometric processing or other transformation is necessary. When
 * working on response elements, individual AccessResponses are moved to the TQM
 * from the AQM by the RB, and necessary transformations are added. These
 * transformations are then executed depending on the overall execution strategy
 * and on pre- and postconditions of the individual transformations.
 * Specifically, the responsibilities of the TQM are:
 * <ul>
 * <li>Discovery and Management of relationships between parts of a set of
 * HarmonizationRequests, i.e. application of a non-atomic Transformer only
 * after all partial responses have arrived</li>
 * <li>Delegation of DataAccessRequests to the AccessQueueMananger if a
 * transformer requires additional input that depended on other input.</li>
 * </ul>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface TransformationQueueManager {

	/**
	 * This operation invokes a synchronous execution of the given transformer.
	 * This requires that all preconditions of that Transformer are satisfied
	 * and have already been injected into it.
	 * 
	 * @param t
	 *            the Transformer which is to be executed.
	 * @return the MediatorResponse for the given Transformer.
	 */
	public MediatorResponse addAndExecuteProcess(Transformer t);

	/**
	 * This method adds a Transformer/Workflow
	 * 
	 * @param t
	 *            the Transformer which is to be added to the execution queue.
	 * @return the UUID that has been assigned to the process and which will
	 *         later be used by teh TQM to signal the availability of the actual
	 *         result.
	 */
	public UUID addProcess(Transformer t);

	/**
	 * This operation is used to request the processing status of a certain
	 * process. It is mainly useful in implementations where the RB needs to
	 * poll it's processes as described in the WPS specification. In truly
	 * asynchronic communication, the TQM instead sends a notification to the RB
	 * when it is finished.
	 * 
	 * @param id
	 *            the UUID of the Process for which the status shall be
	 *            returned.
	 * @return the ProcessStatus of the process whose UUID was given.
	 * @throws UnknownProcessException
	 *             if no Process with the given ID is known to the TQM.
	 */
	public ProcessStatus getStatus(UUID id) throws UnknownProcessException;

	/**
	 * @param id
	 *            the UUID identifying a process managed by the TQM.
	 * @return the MediatorResponse containing the result of the transformation
	 *         process specified.
	 * @throws IncompleteResponseException
	 *             if the specified Process ID does not correspond to a
	 *             completed process.
	 * @throws UnknownProcessException
	 *             if the given ID is not known to the TQM.
	 */
	public MediatorResponse getResult(UUID id)
			throws IncompleteResponseException, UnknownProcessException;

	/**
	 * @param id
	 *            the UUID identifying a process managed by the TQM.
	 * @param ares
	 *            the AccessResponse corresponding to a certain precondition
	 *            expressed earlier. Note thet the AccessResponse's identifier
	 *            is used to correlate the AccessResponse to the precondition
	 *            that requested it.
	 * @return the ProcessStatus that the identified process is in after
	 *         assiging the precondition. This is mostly returned for
	 *         information purposes.
	 * @throws UnknownProcessException
	 *             if the process with the given id is unknown to the TQM.
	 */
	public ProcessStatus notifyProcess(UUID id, AccessResponse ares)
			throws UnknownProcessException;

	/**
	 * This method is a convenience method to add a simple, one-step
	 * transformation of an ConceptualSchema to the TQM.
	 * 
	 * @param im
	 *            the ConceptualSchema to be transformed.
	 * @param t
	 *            the Transformer to be applied to the ConceptualSchema, set up
	 *            with the mapping information or the IDs of source and target
	 *            models.
	 * @return an Identifier for the IM transformation process that has been
	 *         added and that can be reused to ask for the process status or the
	 */
	public UUID addProcess(ConceptualSchema im, Transformer t);

	/**
	 * This enumeration summarizes the states that a certain Transformation can
	 * be in.
	 */
	public enum ProcessStatus {

		/**
		 * At least one precondition satisfied with an access response, but
		 * waiting for other preconditions to be fulfilled
		 * 
		 */
		waiting,

		/**
		 * All Transformer's preconditions satisfied but it is not yet being
		 * executed
		 */
		ready,

		/**
		 * This Transformer is currently being executed. It is signified by the
		 * presence of a process UUID returned when the executeProcess method is
		 * called on the Transfomer. Thus the process method must throw an
		 * "ErrorDuringExecution"
		 */
		processing,

		/**
		 * there was an error during execution that could not be recovered
		 * 
		 * 
		 */
		failed,

		/**
		 * The process has been completed successfully TODO: How do you check
		 * for this
		 */
		completed,

	}
}
