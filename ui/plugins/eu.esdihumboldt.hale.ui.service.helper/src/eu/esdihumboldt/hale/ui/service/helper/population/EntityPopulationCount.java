package eu.esdihumboldt.hale.ui.service.helper.population;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Helper class for Population Service
 * 
 * @author Arun
 */
public class EntityPopulationCount {

	private final IPopulationUpdater notifier;

	/**
	 * Constructor
	 * 
	 * @param notifier {@link IPopulationUpdater}
	 */
	public EntityPopulationCount(IPopulationUpdater notifier) {
		this.notifier = notifier;
	}

	/**
	 * Count the population for the properties of the given group.
	 * 
	 * @param group the group
	 * @param groupDef the group entity definition
	 */
	public void addToPopulation(Group group, EntityDefinition groupDef) {
		Iterable<? extends EntityDefinition> children = this.notifier.getChildren(groupDef);
		if (children != null && children.iterator().hasNext()) {
			for (EntityDefinition def : children) {
				evaluateContext(group, def);
			}
		}
		else {
			evaluateContext(group, groupDef);
		}
	}

	/**
	 * Count population of {@link EntityDefinition}
	 * 
	 * @param entityDef a entity definition
	 * @param instance an instance
	 */
	public void countPopulation(EntityDefinition entityDef, Instance instance) {
		List<ChildContext> path = entityDef.getPropertyPath();
		if (path == null || path.isEmpty()) {
			if (entityDef.getFilter() == null || entityDef.getFilter().match(instance)) {
				increase(entityDef, 1);
				addToPopulation(instance, entityDef);
			}
		}
		else {
			evaluateChildEntityDefinition(instance, entityDef, path);
		}
	}

	/**
	 * count the population for the given entity with all contexts
	 * 
	 * @param group A {@link Group}
	 * @param groupDef An {@link EntityDefinition}
	 */
	private void evaluateContext(Group group, EntityDefinition groupDef) {

		List<ChildContext> path = groupDef.getPropertyPath();

		if (path != null && !path.isEmpty()) {
			ChildContext context = path.get(path.size() - 1);
			Object[] values = group.getProperty(context.getChild().getName());
			if (values != null) {
				// apply the possible source contexts
				if (context.getIndex() != null) {
					// select only the item at the index
					int index = context.getIndex();
					if (index < values.length) {
						values = new Object[] { values[index] };
					}
					else {
						values = new Object[] {};
					}
				}
				if (context.getCondition() != null) {
					// select only values that match the condition
					List<Object> matchedValues = new ArrayList<Object>();
					for (Object value : values) {
						if (AlignmentUtil.matchCondition(context.getCondition(), value, group)) {
							matchedValues.add(value);
						}
					}
					values = matchedValues.toArray();
				}

				if (context.getChild().getName().equals(groupDef.getDefinition().getName())) {
					increase(groupDef, values.length);
				}

				for (Object value : values) {
					if (value instanceof Group) {
						addToPopulation((Group) value, groupDef);
					}
				}
			}
			else {
				increase(groupDef, 0);
			}
		}
	}

	private void evaluateChildEntityDefinition(Group group, EntityDefinition groupDef,
			List<ChildContext> path) {
		if (path.size() == 1) {
			evaluateContext(group, groupDef);
		}
		else {
			ChildContext context = path.get(0);
			List<ChildContext> subPath = path.subList(1, path.size());
			Object[] values = group.getProperty(context.getChild().getName());
			if (values != null) {
				for (Object value : values) {
					if (value instanceof Group) {
						evaluateChildEntityDefinition((Group) value, groupDef, subPath);
					}
				}
			}
			else {
				evaluateChildEntityDefinition(group, groupDef, subPath);
			}
		}
	}

	private void increase(EntityDefinition def, int count) {
		notifier.increaseForEntity(def, count);
	}

}
