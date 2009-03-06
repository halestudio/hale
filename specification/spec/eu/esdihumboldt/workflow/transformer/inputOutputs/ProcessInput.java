package eu.esdihumboldt.workflow.transformer.inputOutputs;

import eu.esdihumboldt.informationgrounding.requesthandler.GroundingService;
import eu.esdihumboldt.workflow.processdescription.InputDescription;

import eu.esdihumboldt.workflow.repository.Transformer;
import java.util.UUID;

/**
 * Defines a process input/output
 * @author mgone
 */
public interface ProcessInput {

    /**
     * @return the {@link UUID} uniquely identifying this {@link input or output}.
     */
    public UUID getIdentifier();

    /**
     * @return the process input identifier for identifying this {@link input}.
     */
    public String getInputIdentifier();

    /**
     * Describes this input
     * @return the {@link  InputDescription}
     */
    public InputDescription getInputDescription();

    /**
     * This method is used to evaluate if this input is compatible to a given postcondiction(output)
     * @param _postcondition output from the source Transformer to compare with this.
     * @return true if the two are compatible
     */
    public boolean isCompatible(ProcessOutput _postcondition);

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
     * This method is used to define the source of this precondition ie the Postcondition which
     * satisfies this precondition.
     * @return The target of this ProcessInput
     */
    public ProcessOutput getSourcePostcondition();

    /**
     *Adds a given grounding service to this precondition
     * @param grounding
     */
    public void setGroundingForPrecondition(GroundingService grounding);

    /**
     * Retrieve the grounding service that satisfies this precondition
     * @return
     */
    public GroundingService getGroundingForPrecondition();

    /**
     * This method is used to define the source postcondition of this input
     * @param postcondition The source postcondition that satisfies this precondition
     */
    public void setSourcePostcondition(ProcessOutput postcondition);

    /**
     * This method is a convinience method for identifying which Transformer the precondition belongs
     * to.
     * @return
     */
    public Transformer getParentTransformer();

    /**
     * This method sets the parent Transformer to which this input belongs to
     * @param _parent
     */
    public void setParentTransformer(Transformer _parent);
}

