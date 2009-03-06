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

package eu.esdihumboldt.workflow.repository;

import eu.esdihumboldt.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.workflow.processdescription.Description;
import eu.esdihumboldt.workflow.transformer.inputOutputs.ProcessInput;
import java.util.Set;
import java.util.UUID;

/**
 * A Workflow is an aggregation of transformers that in total represent a user's goal. A workflow is
 * also with respect to it's external representation a Transformer and can thus be handled just like
 * a simple transformer by the  TransformationQueueManager.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface Workflow {
    /**
     * A unique identifier for this Basic workflow
     * @return
     */
    public UUID getWorkflowID();
    /**Retrieves a task concept that this workflow represents
     * 
     * @return
     */
    public Concept getTaskConcept();

    /**
     * This operation can be used at workflow construction time to insert a
     * new workflow element identified through its precondition and  postcondition.
     * This method also links the inserted Tranformer postcondition to a
     * given Precondition of an existing Transfomer element in the workflow chain
     * 
     * @param source The Transformer to be inserted
     * @param target for the output of the inserted transfomer
     */
    public void insertTransformer(Transformer source,
            ProcessInput target);

    /**This is a convinient method for adding a tranfomer to a workflow
     * The inserted transfomer is not linked to any exiting transfomer in the workflow
     * @param transformer
     */
    public void insertTransformer(Transformer transformer);

    /**This method id used to retrieve a set of transfomer belonging to a workflow
     *
     * @return Transformer set in the workflow
     * @throws NullPointerException
     */
    public Set<Transformer> getTransformers()throws NullPointerException;

    /**This method id used to retrieve a set of transfomer connectors belonging to a workflow
     *
     * @return Transformer set in the workflow
     * @throws NullPointerException
     */
    public Set<TransformerConnector> getTransformerConnectors() throws NullPointerException;

    /**
     * Retrieves the workflow description
     * @return
     * @throws NullPointerException
     */
    public Description getWorkflowDescription() throws NullPointerException;

}
