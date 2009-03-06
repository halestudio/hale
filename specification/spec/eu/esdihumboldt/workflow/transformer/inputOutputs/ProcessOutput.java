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
package eu.esdihumboldt.workflow.transformer.inputOutputs;

import eu.esdihumboldt.mediator.TransformationQueueManager.ProcessStatus;
import eu.esdihumboldt.workflow.processdescription.InputDescription;
import eu.esdihumboldt.workflow.repository.Transformer;
import java.util.UUID;

/**
 *
 * @author mgone
 */
public interface ProcessOutput {

    /**
     * This method is used to define the target of a postcondition
     * @return The target of this ProcessInput
     */
    public ProcessInput getPostconditionTarget();

    /**
     *
     * @return the precondition status of this Postcondition
     */
    public PostconditionStatus getPostconditionStatus();

    /**
     * @return the {@link UUID} uniquely identifying this {@link output}.
     */
    public UUID getUniqueId();

    /**
     * @return the process input/output identifier for identifying this {@link input or output}.
     */
    public String getOutputIdentifier();

    /**
     * Describes this output
     * @return the {@link  InputDescription}
     */
    public InputDescription getOutputDescription();

    /**
     * This method is used to evaluate if this output is compatible to an input in the target Transformer
     * @param precondition the input or output to compare with this.
     * @return true if the two are compatible
     */
    public boolean isCompatible(ProcessInput precondition);

    /**
     * Each of the Preconditions a Transformer has a certain status which is
     * important for the status of the Transformer during construction time.
     * For possible status values during the whole lifecycle of the {@link Transformer},
     * please refer to the {@link ProcessStatus} description.
     */
    public enum PostconditionStatus {

        /**
         * There is no Transformer or AccessRequest satisfying this
         * Precondition yet.
         */
        unsatisfied,
        /**
         * Either a Transformer or a grounding has been attached that
         * satisfies this Precondition.
         */
        satisfied,
    }

    /**
     * Returns the Transformer for whihc this postcondition belongs to
     * @return
     */
    public Transformer getParentTransformer();

    /**
     * This method is used to define the target of this ProcessOutput

     * @param _target The target precondition where this process input output is directed to
     */
    public void setPostconditionTarget(ProcessInput _target);

}
