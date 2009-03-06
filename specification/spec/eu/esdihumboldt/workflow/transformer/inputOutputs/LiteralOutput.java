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

import eu.esdihumboldt.workflow.transformer.domaintypes.DomainMetadata;
import eu.esdihumboldt.workflow.transformer.domaintypes.LiteralValuesChoice;
import java.util.Set;

/**
 *
 * @author mgone
 */
public interface LiteralOutput extends ProcessOutput{
    /**Returns the DataType of this output(or input)
     * Defines the data type of the literal data
     * @return
     */
    public DomainMetadata getDataType();

    /**List of Units of measure supported of this numerical output
     *
     * @return a list of supported UOMs or null if not defined or applicable
     */
    public SupportedUOMs getSupportedUOM();

    /**Identifies type of literal input and provide supporting information
     * Defines the value of the literal data
     * @return
     */
    public LiteralValuesChoice getDefaultValue();
    /**List of values supported by this output
     *
     * @return a list of supported UOMs
     */
    public Set<LiteralValuesChoice> getAllowedValues();
    /**
     * Sets the supported values of the literal output
     * @param _supportedvalues
     */
    public void setSupportedValue(Set<LiteralValuesChoice> _supportedvalues);
    /**
     * Defines the supported unit of measure for numerical literal Data types
     * @param _unitOfMeasure
     */
    public void setSupportedUOM(SupportedUOMs _unitOfMeasure);

}
