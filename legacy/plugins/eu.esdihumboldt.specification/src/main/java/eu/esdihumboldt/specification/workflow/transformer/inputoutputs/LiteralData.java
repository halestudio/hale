package eu.esdihumboldt.specification.workflow.transformer.inputoutputs;

import java.util.Set;

import eu.esdihumboldt.specification.workflow.transformer.domaintypes.LiteralValuesChoice;

public interface LiteralData {

	/**
	 * Returns the DataType of this output(or input) Defines the data type of
	 * the literal data
	 * 
	 * @return
	 */
	public String getDataType();

	/**
	 * List of Units of measure supported of this numerical output (or input).If
	 * one is not defined, a null is returned
	 * 
	 * @return a list of supported UOMs or null if not defined or applicable
	 * 
	 */
	public MeasureUnit getSupportedUnit();

	/**
	 * Identifies type of literal input and provide supporting information
	 * Defines the value of the literal data
	 * 
	 * @return
	 */
	public LiteralValuesChoice getDefaultValue();

	/**
	 * List of values supported by this input/output
	 * 
	 * @return a list of supported UOMs
	 */
	public Set<LiteralValuesChoice> getAllowedValues();

	/**
	 * Sets the supported values of the literal input/output.
	 * 
	 * @param _supportedvalues
	 */
	public void setSupportedValue(Set<LiteralValuesChoice> _supportedvalues);

	/**
	 * Defines the supported unit of measure for numerical literal Data types
	 * 
	 * @param _unitOfMeasure
	 */
	public void setSupportedUnit(MeasureUnit _unitOfMeasure);

}
