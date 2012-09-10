package eu.esdihumboldt.specification.workflow.transformer.inputoutputs;

import eu.esdihumboldt.specification.informationgrounding.requesthandler.GroundingService;
import eu.esdihumboldt.specification.workflow.exceptions.ProcessInputOutputException;
import eu.esdihumboldt.specification.workflow.repository.Transformer.InputOutputStatus;

/**
 * Defines a process input/output
 * 
 * @author mgone
 */
public interface ProcessInput extends InputOutput {

	/**
	 * This method is used to define the source of this precondition ie the
	 * Postcondition which satisfies this precondition.
	 * 
	 * @return The target of this ProcessInput
	 */
	public ProcessOutput getSourcePostcondition();

	/**
	 * Adds a given grounding service to this precondition
	 * 
	 * @param grounding
	 * @throws ProcessInputOutputException
	 */
	public void setGroundingForPrecondition(GroundingService grounding)
			throws ProcessInputOutputException;

	/**
	 * Retrieve the grounding service that satisfies this precondition
	 * 
	 * @return
	 * @throws ProcessInputOutputException
	 */
	public GroundingService getGroundingForPrecondition()
			throws ProcessInputOutputException;

	/**
	 * Returns the status of this input
	 * 
	 * @return
	 */
	public InputOutputStatus getInputStatus();
}
