package eu.esdihumboldt.specification.workflow.transformer.inputoutputs;

import java.io.Serializable;
import java.util.UUID;

import eu.esdihumboldt.specification.workflow.exceptions.IncompatibleTransformersException;
import eu.esdihumboldt.specification.workflow.process.Description;
import eu.esdihumboldt.specification.workflow.repository.Transformer;

/**
 * @author mgone
 */
public interface InputOutput extends Serializable {

	/**
	 * @return the {@link UUID} uniquely identifying this {@link input or
	 *         output}.
	 */
	public UUID getUniqueId();

	/**
	 * @return the process input/output identifier for identifying this
	 *         {@link input}.
	 */
	public String getIdentifier();

	/**
	 * Describes this input/output
	 * 
	 * @return the {@link InputDescription}
	 */
	public Description getDescription();

	/**
	 * This method is a convinience method for identifying which Transformer the
	 * precondition belongs to.
	 * 
	 * @return
	 */
	public Transformer getParentTransformer();

	/**
	 * This method sets the parent Transformer to which this input belongs to
	 * 
	 * @param _parent
	 */
	public void setParentTransformer(Transformer _parent);

	/**
	 * This method determines the compatibility of this input/output to another
	 * input/output
	 * 
	 * @param inputOutput
	 * @return
	 * @throws IncompatibleTransformersException
	 * 
	 */
	public boolean isCompatible(InputOutput inputOutput)
			throws IncompatibleTransformersException;
}
