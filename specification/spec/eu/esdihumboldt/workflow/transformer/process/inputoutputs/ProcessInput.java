package eu.esdihumboldt.workflow.transformer.process.inputoutputs;

import eu.esdihumboldt.informationgrounding.requesthandler.GroundingService;


/**
 * Defines a process input/output
 * @author mgone
 */
public interface ProcessInput extends InputOutput {

    /**
     * This method is used to define the source of this precondition ie the Postcondition which
     * satisfies this precondition.
     * @return The target of this ProcessInput
     */
    public ProcessOutput getSourcePostcondition();

    /**
     * This method is used to define the source postcondition of this input
     * @param postcondition The source postcondition that satisfies this precondition
     */
    public void setSourcePostcondition(ProcessOutput postcondition);

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
}

