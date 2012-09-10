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
package eu.esdihumboldt.specification.mediator.context;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;

/**
 * The Context Interface allows access to all context details:
 * <ul>
 * <li>Context ID</li>
 * <li>Default Context</li>
 * <li>Organization Context</li>
 * <li>User Context</li>
 * </ul>
 * 
 * This interface represents the elements that are persisted in the
 * ContextService, while the HarmonisationContext also contains per-request,
 * dynamic elements.
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: Context.java,v 1.6 2007-11-16 12:51:16 pitaeva Exp $
 */
public interface Context {

	/**
	 * @return the UUID for this context.
	 */
	public UUID getContextID();

	/**
	 * @return this Context's ContextType.
	 */
	public ContextType getContextType();

	/**
	 * @return a title
	 */
	public String getTitle();

	/**
	 * @return this operation can be used to return all constraints of this
	 *         context, ordered by their ContextType.
	 */
	public Map<ContextType, Set<Constraint>> getAllConstraints();

	/**
	 * @param type
	 *            the ContextType whose constraints shall be returned.
	 * @return the Set of Constraints defined for the given ContextType. This
	 *         operation will return just the constriants of that level and not
	 *         take into account overwriting or additional constraints defined
	 *         at other levels.
	 */
	public Set<Constraint> getAllConstraints(ContextType type);

	/**
	 * @param type
	 *            type the ContextType whose combined constraints shall be
	 *            returned.
	 * @return a Set of Constraints that has been created by combining the
	 *         subsets of Constraints. This combination is done to the specified
	 *         scope, i.e. if type = Organisation, Default and Organisation
	 *         Constraints will be combined. For the combination, Organisation
	 *         Constraints can overwrite Default Constraints, and User
	 *         Constriants can overwrite Organisation Constraints, except when a
	 *         Constraint is declared final.
	 */
	public Set<Constraint> getCombinedConstraints(ContextType type);

	/**
	 * The ContextType defines of what Type a certain Context is. If it is
	 * complete, it will contain a Default, Organisation and User Context.
	 */
	public enum ContextType {
		/**
		 * Default context, includes all constraints to build standard WMC
		 */
		Default,

		/**
		 * Organization Context includes link to Default Context as a parent
		 * context and organization-owner of this context.
		 */
		Organisation,

		/**
		 * Organization Context includes link to Organization Context as a
		 * parent context and user-owner of this context.
		 */

		User,

		/**
		 * currently not in use.
		 */
		Complete
	}

	/**
	 * for hibernate use only.
	 * 
	 * @return unique identifier for the database.
	 */
	public long getId();

	/**
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id);
}
