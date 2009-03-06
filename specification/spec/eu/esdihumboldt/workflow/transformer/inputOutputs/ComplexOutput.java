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

import eu.esdihumboldt.mediator.TypeKey;
import eu.esdihumboldt.mediator.constraints.Constraint;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mgone
 */
public interface ComplexOutput extends ProcessOutput{

	/**
	 * @param key the identifier of a certain {@link Constraint}, i.e. as a
	 * {@link ConstraintType}.
	 * @return the {@link Constraint} identified by this key, or null if no such
	 * {@link Constraint} was found.
	 */
	public Constraint getConstraint(TypeKey key) throws NullPointerException;

    /**
     * This methods is used to define the set of constraints for this input/output
     * @param _constraints a Map of Constraint ytpe and the constraints
     * @return
     */
    public void setConstraints(Map<TypeKey, Constraint> _constraints);
	/**
	 * @return a Map with all the Constraints in this Request. The keys used
	 * represent the branch of the concrete Interface that has been
	 * implemented by the Constraint used as value. This means that for each
	 * subinterface of Constraint, exactly one Constraint can be in the returned
     * map.
     * @throws NullPointerException
	 */
	public Map<TypeKey, Constraint> getConstraints() throws NullPointerException;

    /**
     * Describes the defaul format of the complex data input/output type
     * @return
     */
    public Format getDefaultFormat();

    /**
     * Retrieves a Set of supported formats for this input
     * @return
     * @throws NullPointerException
     */
    public Set<Format> getSupportedFormats()throws NullPointerException;
    /**
     * sets the supported formats for this input/output
     * @param _supportedFormats
     */
    public void setSupportedFormats(Set<Format> _supportedFormats);

}
