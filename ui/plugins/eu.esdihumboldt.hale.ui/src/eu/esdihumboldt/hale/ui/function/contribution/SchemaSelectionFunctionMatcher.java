/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.function.contribution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameter;
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.condition.EntityCondition;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyCondition;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Checks if a function is applicable for a schema selection.
 * 
 * @author Simon Templer
 */
public class SchemaSelectionFunctionMatcher {

	private final boolean ignoreTarget;

	private final boolean ignoreSource;

	/**
	 * Default constructor.
	 */
	public SchemaSelectionFunctionMatcher() {
		this(false, false);
	}

	/**
	 * Constructor.
	 * 
	 * @param ignoreSource if the source items should be ignored
	 * @param ignoreTarget if the target items should be ignored
	 */
	public SchemaSelectionFunctionMatcher(boolean ignoreSource, boolean ignoreTarget) {
		super();
		this.ignoreTarget = ignoreTarget;
		this.ignoreSource = ignoreSource;
	}

	/**
	 * Determine if the given function is applicable for the given selection.
	 * 
	 * @param function the function to test
	 * @param selection the schema selection
	 * @return <code>true</code> if the definition matches the selection,
	 *         <code>false</code> otherwise
	 * @throws IllegalStateException if the function type is unknown (neither a
	 *             type nor a property function)
	 */
	public boolean matchFunction(FunctionDefinition<?> function, SchemaSelection selection) {
		if (function instanceof TypeFunctionDefinition) {
			TypeFunctionDefinition tf = (TypeFunctionDefinition) function;
			// match selection against function definition
			return matchTypeFunction(tf, selection);
		}
		else if (function instanceof PropertyFunctionDefinition) {
			PropertyFunctionDefinition pf = (PropertyFunctionDefinition) function;
			// match selection against function definition
			return matchPropertyFunction(pf, selection);
		}
		else {
			throw new IllegalStateException("Unsupported function type encountered");
		}
	}

	/**
	 * Test if the given function definition matches the given selection
	 * 
	 * @param function the function definition
	 * @param selection the schema selection
	 * @return <code>true</code> if the definition matches the selection,
	 *         <code>false</code> otherwise
	 */
	public boolean matchPropertyFunction(PropertyFunctionDefinition function,
			SchemaSelection selection) {
		/*
		 * Deactivated restriction that property cells can only be created with
		 * existing type cells.
		 */
//		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
//				AlignmentService.class);
//
//		if (!AlignmentUtil.hasTypeRelation(as.getAlignment())) {
//			// don't allow creating property relations if there are no type
//			// relations present
//			return false;
//		}

		if (selection == null || selection.isEmpty()) {
			// for no selection always allow creating a new cell if there are
			// type relations present
			return true;
		}

		Set<EntityDefinition> sourceItems = selection.getSourceItems();
		Set<EntityDefinition> targetItems = selection.getTargetItems();

		// check types
		if (!ignoreSource && !checkType(sourceItems, PropertyEntityDefinition.class)) {
			if (checkType(sourceItems, TypeEntityDefinition.class)) {
				/*
				 * Allow types selected as source, handle as if no source is
				 * selected
				 */
				sourceItems = new HashSet<EntityDefinition>();
			}
			else
				return false;
		}
		if (!ignoreTarget && !checkType(targetItems, PropertyEntityDefinition.class)) {
			return false;
		}

		// TODO check if properties have the same parent type? what about joins?

		// check counts
		if (!ignoreSource && !checkCount(sourceItems.size(), function.getSource(), false)) {
			return false;
		}
		if (!ignoreTarget && !checkCount(targetItems.size(), function.getTarget(), true)) {
			return false;
		}

		// check mandatory source/target with special conditions
		if (!ignoreSource && !checkMandatoryConditions(sourceItems, function.getSource())) {
			return false;
		}
		if (!ignoreTarget && !checkMandatoryConditions(targetItems, function.getTarget())) {
			return false;
		}

		// TODO other checks?

		return true;
	}

	/**
	 * Checks if all entities that are mandatory in the function definition and
	 * have specific attached conditions can be met by at least one of the given
	 * schema entities.
	 * 
	 * @param schemaEntities the schema entities
	 * @param functionEntities the entities as defined in the function
	 * @return if the conditions on mandatory function entities can be met
	 */
	protected boolean checkMandatoryConditions(Set<EntityDefinition> schemaEntities,
			Iterable<? extends ParameterDefinition> functionEntities) {
		for (ParameterDefinition functionEntity : functionEntities) {
			if (functionEntity.getMinOccurrence() != 0) {
				// entity is mandatory

				if (functionEntity instanceof PropertyParameter) {
					PropertyParameterDefinition pp = (PropertyParameterDefinition) functionEntity;
					if (!pp.getConditions().isEmpty()) {
						// check if there is an entity given that matches the
						// conditions
						if (!checkConditions(pp.getConditions(), schemaEntities)) {
							// conditions not met
							return false;
						}
					}
				}
				else if (functionEntity instanceof TypeParameter) {
					TypeParameterDefinition tp = (TypeParameterDefinition) functionEntity;
					if (!tp.getConditions().isEmpty()) {
						// check if there is an entity given that matches the
						// conditions
						if (!checkConditions(tp.getConditions(), schemaEntities)) {
							// conditions not met
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Check if the given conditions can be met by at least on of the given
	 * schema entities.
	 * 
	 * @param conditions the conditions to be met
	 * @param schemaEntities the schema entities to test
	 * @return if the conditions are met by one of the entities
	 */
	private boolean checkConditions(List<? extends EntityCondition<?>> conditions,
			Set<EntityDefinition> schemaEntities) {
		for (EntityDefinition entity : schemaEntities) {
			boolean entityValid = true;
			for (EntityCondition<?> condition : conditions) {
				if (!checkCondition(condition, entity)) {
					entityValid = false;
				}
			}
			if (entityValid) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given condition is met by the given entity.
	 * 
	 * @param condition the condition
	 * @param entity the entity to test
	 * @return if the entity meets the condition
	 */
	private boolean checkCondition(EntityCondition<?> condition, EntityDefinition entity) {
		if (condition instanceof PropertyCondition) {
			if (entity instanceof PropertyEntityDefinition) {
				Property p = new DefaultProperty((PropertyEntityDefinition) entity);
				return ((PropertyCondition) condition).accept(p);
			}
		}
		else if (condition instanceof TypeCondition) {
			if (entity instanceof TypeEntityDefinition) {
				Type t = new DefaultType((TypeEntityDefinition) entity);
				return ((TypeCondition) condition).accept(t);
			}
		}

		return false;
	}

	/**
	 * Test if the given function definition matches the given selection
	 * 
	 * @param function the function definition
	 * @param selection the schema selection
	 * @return <code>true</code> if the definition matches the selection,
	 *         <code>false</code> otherwise
	 */
	public boolean matchTypeFunction(TypeFunctionDefinition function, SchemaSelection selection) {
		// check types
		Set<EntityDefinition> sourceItems = selection.getSourceItems();
		if (!ignoreSource && !checkType(sourceItems, TypeEntityDefinition.class)) {
			return false;
		}
		Set<EntityDefinition> targetItems = selection.getTargetItems();
		if (!ignoreTarget && !checkType(targetItems, TypeEntityDefinition.class)) {
			return false;
		}

		// check counts
		if (!ignoreSource && !checkCount(sourceItems.size(), function.getSource(), true)) {
			return false;
		}
		if (!ignoreTarget && !checkCount(targetItems.size(), function.getTarget(), false)) {
			return false;
		}

		// check mandatory source/target with special conditions
		if (!ignoreSource && !checkMandatoryConditions(sourceItems, function.getSource())) {
			return false;
		}
		if (!ignoreTarget && !checkMandatoryConditions(targetItems, function.getTarget())) {
			return false;
		}

		// TODO other checks?

		return true;
	}

	/**
	 * Checks if each item is of the given type
	 * 
	 * @param items the items
	 * @param type the type
	 * @return <code>true</code> if all items are of the given type
	 */
	protected boolean checkType(Iterable<?> items, Class<?> type) {
		for (Object item : items) {
			if (!type.isAssignableFrom(item.getClass())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if the given entity count is compatible with the given set of
	 * entity definitions
	 * 
	 * @param count the entity count
	 * @param entities the entity definitions
	 * @param isTarget if the entities are target entities
	 * @return if then entity count is compatible with the definitions
	 */
	protected boolean checkCount(int count, Set<? extends ParameterDefinition> entities,
			boolean isTarget) {
		int min = 0;
		int max = 0;

		for (ParameterDefinition param : entities) {
			min += param.getMinOccurrence();
			if (max != AbstractParameter.UNBOUNDED) {
				int pMax = param.getMaxOccurrence();
				if (pMax == AbstractParameter.UNBOUNDED) {
					max = pMax;
				}
				else {
					max += pMax;
				}
			}
		}

		// check minimum
		if (count < min) {
			return false;
		}

		if (max == 0 && !isTarget) {
			// allow augmentations
			return true;
		}

		// check maximum
		if (max != AbstractParameter.UNBOUNDED && count > max) {
			return false;
		}

		return true;
	}

}
