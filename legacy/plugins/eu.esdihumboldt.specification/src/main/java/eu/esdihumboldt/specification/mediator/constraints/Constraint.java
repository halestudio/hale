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
package eu.esdihumboldt.specification.mediator.constraints;

import java.io.Serializable;
import java.util.UUID;

/**
 * This is the superinterface for all constriants used in the HUMBOLDT
 * Framework. A constraint expresses a requirement that has been formulated by
 * the user regarding a specific request that user made to the system. There are
 * several different sources for a constraint:
 * <ul>
 * <li>The request itself</li>
 * <li>The user's context</li>
 * <li>The organization's context</li>
 * <li>The default context</li>
 * </ul>
 * When implementing constraints, it is encouraged to implement them as
 * immutable objects (i.e. all fields final). Constraints are being used by the
 * RequestBroker and the InformationGroundingService to decide which data
 * sources need to be accessed with which query parameters and also to define
 * which transformation need to be applied to a data set before returning it to
 * the client.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: Constraint.java,v 1.3 2007-11-15 12:26:09 pitaeva Exp $
 */
public interface Constraint extends Serializable {

	/**
	 * @return true if this Constraint was satisfied.
	 */
	public boolean isSatisfied();

	/**
	 * @return the {@link ConstraintSource} for this {@link Constraint}.
	 */
	public ConstraintSource getConstraintSource();

	/**
	 * These types identify various kinds of Constraints with respect of their
	 * origin.
	 */
	public enum ConstraintSource {

		/**
		 * Information that is gather from the payload of the request and
		 * constitutes the actual query parameters.
		 */
		parameter,
		/**
		 * Information that is gathered from the protocol layer of the request.
		 * This contains things like the HTTP_env variables.
		 */
		protocol,
		/**
		 * Information retrieved from the Request's context, usually through the
		 * ContextService.
		 */
		context
	}

	/**
	 * @return id unique identifier for the database.
	 */
	public long getId();

	/**
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id);

	/**
	 * A unique identifier (UUID).
	 * 
	 * @return
	 */
	public UUID getIdentifier();

	/**
	 * A unique identifier (UUID).
	 * 
	 * @param identifier
	 */
	public void setIdentifier(UUID identifier);

	/**
	 * This method checks to see if a certain constraint can be overwritten or
	 * not during precondition concretization The default is protected.
	 * 
	 * @return true if write protected
	 */
	public boolean isFinalized();

	/**
	 * Sets the write-able status of the constraint
	 * 
	 * @param write
	 */
	public void setFinalized(boolean write);

	/**
	 * Sets the status of this constraint to be satisfied
	 * 
	 * @param _satisfied
	 */
	public void setSatisfied(boolean _satisfied);

	/**
	 * Checks weather this concstraint is compatible with another constraint
	 * 
	 * @param constraint
	 * @return
	 */
	public boolean compatible(Constraint constraint);

	/**
	 * This method is used to assign a consraint as shared or not
	 * 
	 * @param shared
	 *            Sets this constraint to be shared or not
	 */
	public void setShared(boolean shared);

	/**
	 * This method detnotes whether this consraints is shareable or not
	 * 
	 * @return True if the constraint is shared
	 */
	public boolean isShared();
}
