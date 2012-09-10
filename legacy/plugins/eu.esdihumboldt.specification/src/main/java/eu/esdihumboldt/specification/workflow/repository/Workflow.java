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
package eu.esdihumboldt.specification.workflow.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.MediatorComplexRequest;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.workflow.exceptions.ConcretizationException;
import eu.esdihumboldt.specification.workflow.exceptions.InconsistentWorkflowException;
import eu.esdihumboldt.specification.workflow.process.Description;
import eu.esdihumboldt.specification.workflow.transformer.inputoutputs.ProcessInput;

/**
 * A Workflow is an aggregation of transformers that in total represent a user's
 * goal. A workflow is also with respect to it's external representation a
 * Transformer and can thus be handled just like a simple transformer by the
 * TransformationQueueManager.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Workflow extends Serializable {

	/**
	 * A unique identifier for this Basic workflow
	 * 
	 * @return
	 */
	public UUID getIdentifier();

	/**
	 * Retrieves a task concept that this workflow represents
	 * 
	 * @return
	 */
	public Concept getTaskConcept();

	/**
	 * This is a convinient method for adding a tranfomer to a workflow The
	 * inserted transfomer is not linked to any exiting transfomer in the
	 * workflow
	 * 
	 * @param transformer
	 */
	public void insertTransformer(Transformer transformer);

	/**
	 * This method id used to retrieve a set of transfomer belonging to a
	 * workflow
	 * 
	 * @return Transformer set in the workflow
	 * @throws NullPointerException
	 */
	public List<Transformer> getTransformers() throws NullPointerException;

	/**
	 * This method id used to retrieve a set of transfomer connectors belonging
	 * to a workflow
	 * 
	 * @return Transformer set in the workflow
	 * @throws NullPointerException
	 */
	public Set<Connector> getConnectors() throws NullPointerException;

	/**
	 * Gather all the process inputs (leaf-preconditions) of the Transformers in
	 * this basic workflow that have not been satisfied yet!Leaf preconditions
	 * are all the inputs to the Transformers in the Basic Workflow that are not
	 * yet satisfied at design time and must be satisfied at consstrction time
	 * by either a grounding service(case of perfect match) or by a transformer
	 * ( case of a non- perfect grounding).
	 * 
	 * @return
	 * @throws NullPointerException
	 */
	public Set<ProcessInput> getLeafPreconditions() throws NullPointerException;

	/**
	 * Retrieves the workflow description
	 * 
	 * @return
	 * @throws NullPointerException
	 */
	public Description getDescription() throws NullPointerException;

	/**
	 * This method retrieves the terminal transformer in this Wokflow
	 * 
	 * @return a Terminal Transformer in this workflow
	 * @throws InconsistentWorkflowException
	 */
	public Transformer getTerminalTransformer()
			throws InconsistentWorkflowException;

	/**
	 * Determines if a link is valid or not, i.e a connection can be made
	 * between source (output)and target input
	 * 
	 * @param _sourceTransformer
	 *            link origin
	 * @param _targetPrecondition
	 *            link destination
	 * @return
	 */
	public boolean isValidConnection(Transformer _sourceTransformer,
			ProcessInput _targetPrecondition);

	/**
	 * A convinience method that concretizes preconditions in this basic
	 * workflow given a set of constraints
	 * 
	 * @param mcr
	 *            Mediator complex request with a set of constraints and the
	 *            taskconcept provided in the MCR
	 * @throws ConcretizationException
	 */
	public void concretize(MediatorComplexRequest mcr)
			throws ConcretizationException;

	/**
	 * This method tests whether the workflow is lad or not. A valid basic
	 * workflow has all the leaf preconditions(inputs) satisfied either by a
	 * Transformer or a grounding service
	 * 
	 * @return
	 * @throws RuntimeException
	 */
	public boolean isValid();

	/**
	 * This is a convinient method for adding a tranfomer connecto to a workflow
	 * This action also updates the basic workflow and the target precondition
	 * in the link
	 * 
	 * 
	 * @param link
	 *            the link to be added in this workflow
	 * @return true if link successfully added
	 */
	public boolean addConnector(Connector link);

}
