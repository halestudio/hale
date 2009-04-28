package eu.esdihumboldt.workflow.transformer.inputoutputs;

import eu.esdihumboldt.informationgrounding.requesthandler.GroundingService;
import eu.esdihumboldt.workflow.exceptions.ProcessInputOutputException;


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
     * @throws ProcessInputOutputException
     */
    public void setSourcePostcondition(ProcessOutput postcondition) throws ProcessInputOutputException;

    /**
     *Adds a given grounding service to this precondition
     * @param grounding
     * @throws ProcessInputOutputException
     */
    public void setGroundingForPrecondition(GroundingService grounding)throws ProcessInputOutputException;

    /**
     * Retrieve the grounding service that satisfies this precondition
     * @return
     * @throws ProcessInputOutputException
     */
    public GroundingService getGroundingForPrecondition()throws ProcessInputOutputException;
}

