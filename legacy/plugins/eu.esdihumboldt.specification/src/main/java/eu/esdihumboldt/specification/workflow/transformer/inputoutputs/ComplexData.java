package eu.esdihumboldt.specification.workflow.transformer.inputoutputs;

import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.specification.mediator.TypeKey;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.workflow.repository.Connector;

/**
 * This is a super class for complex data types
 * 
 * @author mgone
 */
public interface ComplexData {

	/**
	 * This method returns a constraint of a given type key
	 * 
	 * @param key
	 *            the identifier of a certain {@link Constraint}, i.e. as a
	 * @link ConstraintType}.
	 * @return the {@link Constraint} identified by this key, or null if no such
	 *         {@link Constraint} was found.
	 * @throws NullPointerException
	 */
	public Constraint getConstraint(TypeKey key) throws NullPointerException;

	/**
	 * This methods is used to define the set of constraints for this
	 * input/output
	 * 
	 * @param _constraints
	 *            a Map of Constraint ytpe and the constraints
	 */
	public void setConstraints(Map<TypeKey, Constraint> _constraints);

	/**
	 * @return a Map with all the Constraints in this Request. The keys used
	 *         represent the branch of the concrete Interface that has been
	 *         implemented by the Constraint used as value. This means that for
	 *         each subinterface of Constraint, exactly one Constraint can be in
	 *         the returned map.The operation may return null only if neither if
	 *         the cpnstraint set has not been defined or an empty map if there
	 *         are no constraints.
	 * @throws NullPointerException
	 * 
	 * 
	 */
	public Map<TypeKey, Constraint> getConstraints()
			throws NullPointerException;

	/**
	 * Describes the defaul format of the complex data input/output type
	 * 
	 * @return
	 */
	public Format getDefaultFormat();

	/**
	 * Retrieves a Set of supported formats for this input
	 * 
	 * @return
	 * @throws NullPointerException
	 */
	public Set<Format> getSupportedFormats() throws NullPointerException;

	/**
	 * sets the supported formats for this input/output
	 * 
	 * @param _supportedFormats
	 */
	public void setSupportedFormats(Set<Format> _supportedFormats);

	/**
	 * Protects the given set of of constraints from being overwritten
	 * 
	 * @param _protectedkeys
	 *            for constraints whihc needs to be protected
	 * @throws RuntimeException
	 */
	public void protectConstraints(Set<TypeKey> _protectedkeys);

	/**
	 * returns true or false depending on whether this complex data had a
	 * connection or not
	 * 
	 * @return
	 */
	public boolean isIsLinked();

	/**
	 * returns a link that is connected to to this complex data
	 * 
	 * @return
	 */
	public Connector getLink();

	/**
	 * Sets a link to this complex data
	 * 
	 * @param link
	 */
	public void setLink(Connector link);

}
