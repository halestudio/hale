/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator;

import java.util.Map;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.Constraint.ConstraintSource;
import eu.esdihumboldt.specification.mediator.context.Context;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;

/**
 * The MediatorComplexRequest is the container- and interface-neutral structure
 * representing the request a client sent to the Mediator node. It contains both
 * the original request and also the context information required for further
 * processing that was retrieved from the ContextService. Part of this
 * information is the user identification and his credentials.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface MediatorComplexRequest {

	/**
	 * @return the {@link UUID} uniquely identifying this
	 *         {@link MediatorComplexRequest}.
	 */
	public UUID getIdentifier();

	/**
	 * @return the task {@link Concept} representing the activity that is being
	 *         requested, such as the creation of a map, the assembly of spatial
	 *         data or some specific processing. There is at least one task
	 *         exposed by each ITF implementation.
	 */
	public Concept getTaskConcept();

	/**
	 * @return the {@link Context} that was attached to this
	 *         MediatorComplexRequest.
	 */
	public Context getContext();

	/**
	 * @param key
	 *            the identifier of a certain {@link Constraint}, i.e. as a
	 *            {@link ConstraintType}.
	 * @return the {@link Constraint} identified by this key, or null if no such
	 *         {@link Constraint} was found.
	 */
	public Constraint getConstraint(TypeKey key);

	/**
	 * @return a Map with all the Constraints in this Request. The keys used
	 *         represent the branch of the concrete Interface that has been
	 *         implemented by the Constraint used as value. This means that for
	 *         each subinterface of Constraint, exactly one Constraint can be in
	 *         the returned map.
	 */
	public Map<TypeKey, Constraint> getConstraints();

	/**
	 * @param type
	 *            one of the AttributeTypes specified in
	 *            MediatorComplexRequest.ConstraintSource.
	 * @return A Map containing the Attributes of the specified
	 *         ConstraintSource.
	 */
	public Map<TypeKey, Constraint> getConstraints(ConstraintSource type);

	/**
	 * @return a reference to the {@link InterfaceController} implementation
	 *         object that distributed this {@link MediatorComplexRequest}.
	 */
	public InterfaceController getInitiator();

}
