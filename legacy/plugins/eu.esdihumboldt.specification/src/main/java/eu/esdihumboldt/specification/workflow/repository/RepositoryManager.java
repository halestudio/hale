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
package eu.esdihumboldt.specification.workflow.repository;

import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.workflow.exceptions.InconsistentWorkflowException;
import eu.esdihumboldt.specification.workflow.process.Description;

/**
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @author Moses Gone
 */
public interface RepositoryManager {

	/**
	 * This method is used to retrieves the basic workflow from the WFR and
	 * defines the groundings that are required based on the basic workflow
	 * preconditions.
	 * 
	 * @param taskconcept
	 * @return Workflow a Basic Workfow
	 * @throws InconsistentWorkflowException
	 *             thrown if a valid workflow cannot be returned
	 */
	public Workflow getBasicWorkflow(Concept taskconcept)
			throws InconsistentWorkflowException;

	/**
	 * This method is used to retrieves a Transformer from the Repo
	 * 
	 * @param id
	 *            the Identifier of the retrieved transformer
	 * @return Workflow a Basic Workfow
	 */
	public Transformer getTransformer(UUID id);

	/**
	 * This method is used at design time to create a basic workflow and store
	 * it in the repository, given an MCR.
	 * 
	 * @param description
	 *            The description of the capabilities of the basic workflow
	 * @param transformer
	 *            a set of transformers making up the basic workflow to be
	 *            created
	 * @param taskconcept
	 *            a task concept for identifying this BW
	 */
	public void createBasicWorkflow(Concept taskconcept,
			Description description, Transformer transformer);

	/**
	 * This method deletes the basic workflow from the repository
	 * 
	 * @param id
	 *            The Identfier of the BW to be deleted
	 * @return True if BW is successfully removed
	 */
	public Boolean removeBasicWorkflow(UUID id);

	/**
	 * This method is used for workflow management purpose and is used to edit
	 * and update an existing basic workflow in the repository.
	 * 
	 * @param id
	 *            the identifier of the workflow to be updated
	 * @param workflow
	 *            Workflow with information for update
	 * 
	 */
	public void updateBasicWorkflow(UUID id, Workflow workflow);

	/**
	 * This method is used during Transformer construction to create and add a
	 * Transformer to the repository given a WPS grounding and the process
	 * identifier. The method calls the retrieve- ProcessDescriptor, passes the
	 * rtrieved process description and generates a Transformer. The Transformer
	 * is then added to the repository.
	 * 
	 * @param wpsUrl
	 *            The URL of the WPS service that realizes the Transformer to be
	 *            created
	 * @param processId
	 *            The process identifier of the generated Transormer
	 */
	public void storeTransformer(String wpsUrl, String processId);

	/**
	 * This method updates an existing Transformer using contents of the
	 * supplied Transformer
	 * 
	 * @param id
	 *            the identifier of the transformer to be updated
	 * @param transformer
	 * @return A unique tranformer identifies on the updated Transformer
	 */
	public void updateTransformer(UUID id, Transformer transformer);

	/**
	 * This method is used to find out the Transformers in the Repository
	 * 
	 * 
	 * @return A list of available Transformers in the Repository
	 */
	public Set<Transformer> exploreTransformers();

	/**
	 * A convinient method to retrieve a transformer with the given identifier
	 * 
	 * @param _transformerIdentifier
	 * @return A transformer with he specified name
	 */
	public Transformer getTransformer(String _transformerIdentifier);
}
