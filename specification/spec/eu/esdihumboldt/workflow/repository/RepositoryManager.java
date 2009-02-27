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
package eu.esdihumboldt.workflow.repository;

import eu.esdihumboldt.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.workflow.processdescription.Description;
import eu.esdihumboldt.workflow.processdescription.ProcessDescription;
import eu.esdihumboldt.workflow.repository.Transformer.ProcessType;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @author Moses Gone
 */
public interface RepositoryManager {

    /**
     * This method is used to retrieves the basic workflow from the WFR and
     * defines the groundings  that are required based on the basic workflow
     * preconditions.
     *
     * @param taskconcept 
     * @return Workflow a Basic Workfow
     */
    public Workflow getBasicWorkflow(Concept taskconcept);

    /**
     * This method is used to retrieves a Transformer from the Repo
     *
     * @param id the Identifier of the retrieved transformer 
     * @return Workflow a Basic Workfow
     */
    public Transformer getTransformer(UUID id);

    /**
     * This method is used at design time to create a basic workflow and store
     * it in the repository, given an MCR.
     *
     * @param descr The description of the capabilities of the basic workflow
     * @param transformers a set of transformers making up the basic workflow to be created
     * @param taskconcept a task concept for identifying this BW 
     * @return UUID of the created BW
     */
    public void createBasicWorkflow(Concept taskconcept,
            Description descr, Transformer transformers);

    /**
     * This method deletes the basic workflow from the repository
     *
     * @param id The Identfier of the BW to be deleted
     * @return True if BW is successfully removed
     */
    public Boolean removeBasicWorkflow(UUID id);

    /**
     * This method is used for workflow management purpose and is used to edit
     * and update an existing  basic workflow in the repository.
     *
     * @param id the identifier of the workflow to be updated
     * @param workflow Workflow with information for update
     * @return UUID of the updated basic workflow
     */
    public void updateBasicWorkflow(UUID id, Workflow workflow);

    /**
     * This method is used during Transformer construction to create and add a
     * Transformer to the repository given a set of inputs and outputs expressed
     * as MCRs, and a description of the process
     *
     * @param transformer A newly created Tranformer
     * @param pd A human readeable description of what the Transformer does
     * @return UUID of the Transformer which has been successfully added to the repository
     */
    public void storeTransformer(Transformer transformer);

    /**
     * This method updates an existing Transformer using contents of the supplied
     * Transformer
     *
     * @param id the identifier of the transformer to be updated
     * @param transformer
     * @return A unique tranformer identifies on the updated Transformer
     */
    public void updateTransformer(UUID id, Transformer transformer);

    /**This method is used to find out the Transformers in the Repository
     * 
     *
     * @return A list of available Transformers in the Repository
     */
    public Set<Transformer> exploreTransformers();

    /**
     * 
     * @param _type 
     * @param type a Process type, ie processing transfomer or constraint transfomer
     * @return a set of transfomers of the given processtype
     */
    public Transformer getTransformers(ProcessType _type);
        /**
     * A convinient method to retrieve a transformer with the given name
     * @param _transformerName
     * @return A transformer with he specified name
     */
    public Transformer getTransformer(String _transformerName);
}
