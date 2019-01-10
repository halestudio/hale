/*
 * Copyright (c) 2018 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.align.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;

/**
 * Helper for finding entities matching certain conditions in the entity
 * hierarchy.
 * 
 * @author Simon Templer
 */
public class EntityFinder {

	/**
	 * Value for unbounded number of levels.
	 */
	public static final int UNBOUNDED_LEVELS = -1;

	private final Predicate<EntityDefinition> condition;

	private final int maxLevel;

	/**
	 * Create a new entity finder using the given condition.
	 * 
	 * @param condition the condition that entities should match
	 * @param maxLevel the maxium number of levels to descend
	 */
	public EntityFinder(Predicate<EntityDefinition> condition, int maxLevel) {
		super();
		this.condition = condition;
		this.maxLevel = maxLevel;
	}

	/**
	 * Find entities matching the condition in (and including) the given parent
	 * entities.
	 * 
	 * @param parents the entities to search
	 * @return the entities found matching the condition
	 */
	public List<EntityDefinition> find(Collection<EntityDefinition> parents) {
		List<EntityDefinition> found = new ArrayList<>();

		for (EntityDefinition entity : parents) {
			found.addAll(find(entity));
		}

		return found;
	}

	/**
	 * Find entities matching the condition in (and including) the given parent.
	 * 
	 * @param parent the entity to search
	 * @return the entities found matching the condition
	 */
	public List<EntityDefinition> find(EntityDefinition parent) {
		List<EntityDefinition> found = new ArrayList<>();

		found.addAll(find(parent, Collections.emptySet()));

		return found;
	}

	/**
	 * Find entities matching the condition in (and including) the given parent.
	 * 
	 * @param parent the entity to search
	 * @param checked the set of already checked type definitions (to avoid
	 *            endless recursion)
	 * @return the entities found matching the condition
	 */
	protected List<EntityDefinition> find(EntityDefinition parent, Set<DefinitionGroup> checked) {
		final int level = parent.getPropertyPath().size();
		if (maxLevel != UNBOUNDED_LEVELS && maxLevel < level) {
			return Collections.emptyList();
		}

		// check if type has already been found
		DefinitionGroup typeDef = AlignmentUtil.getDefinitionGroup(parent);
		if (checked.contains(typeDef)) {
			return Collections.emptyList();
		}
		else {
			checked = new HashSet<>(checked);
			checked.add(typeDef);
		}

		List<EntityDefinition> found = new ArrayList<>();

		// add parent if it matches the condition
		if (condition.test(parent)) {
			found.add(parent);
		}

		if (maxLevel != UNBOUNDED_LEVELS && maxLevel < level + 1) {
			return found;
		}

		// check children
		for (EntityDefinition child : AlignmentUtil.getChildrenWithoutContexts(parent)) {
			found.addAll(find(child, checked));
		}

		return found;
	}

}
