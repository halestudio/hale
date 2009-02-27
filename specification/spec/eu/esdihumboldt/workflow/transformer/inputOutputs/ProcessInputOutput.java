package eu.esdihumboldt.workflow.transformer.inputOutputs;

import eu.esdihumboldt.informationgrounding.requesthandler.GroundingService;
import eu.esdihumboldt.workflow.processdescription.InputDescription;

import java.util.UUID;

/**
 * Defines a process input/output
 * @author mgone
 */
public interface ProcessInputOutput {

    /**
     * @return the {@link UUID} uniquely identifying this {@link input or output}.
     */
    public UUID getIdentifier();

    /**
     * @return the process input/output identifier for identifying this {@link input or output}.
     */
    public String getInputOutputIdentifier();

    /**
     * Describes this input
     * @return the {@link  InputDescription}
     */
    public InputDescription getInputDescription();

    /**
     *
     * @return the precondition status of this precondition
     */
    public PreconditionStatus getPreconditionStatus();


    /**
     * Each of the Preconditions a Transformer has a certain status which is
     * important for the status of the Transformer during construction time.
     * For possible status values during the whole lifecycle of the {@link Transformer},
     * please refer to the {@link ProcessStatus} description.
     */
    public enum PreconditionStatus {

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
     * This method is used to define the target of a postcondition if this instance is a postcondition

     * @return The target of this ProcessInputOutput
     */
    public ProcessInputOutput getTarget();

    /**
     *
     * @param precondition
     * @param grounding
     */
    public void setGroundingForPrecondition(GroundingService grounding);

    /**
     * This method is used to evaluate if two inputs or inputs and outputs are
     * compatible
     * @param _inputoutput the input or output to compare with this.
     * @return true if the two are compatible
     */
    public boolean isCompatible(ProcessInputOutput _inputoutput);
    /**
     * Retrieve the grounding for this precondition
     * @return
     */
    public GroundingService getGroundingForPrecondition();
    /**
     * This method is used to define the target of this ProcessInputoutput

     * @param _target The target precondition where this process input output is directed to
     */
    public void setTarget(ProcessInputOutput _target);
}

