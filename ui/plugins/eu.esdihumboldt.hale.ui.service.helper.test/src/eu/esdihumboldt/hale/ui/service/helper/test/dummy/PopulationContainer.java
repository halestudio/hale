/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.ui.service.helper.test.dummy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.helper.population.IPopulationUpdater;
import eu.esdihumboldt.hale.ui.service.helper.population.EntityPopulationCount;

/**
 * Contains the populations of {@link EntityDefinition}s
 * 
 * @author Arun
 */
public class PopulationContainer implements IPopulationUpdater {

	private final Map<EntityDefinition, PopulationImpl> population = new HashMap<EntityDefinition, PopulationImpl>();

	private static EntityPopulationCount populationCount;

	/**
	 * Default constructor
	 */
	public PopulationContainer() {
		populationCount = new EntityPopulationCount(this);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.helper.population.IPopulationUpdater#increaseForEntity(eu.esdihumboldt.hale.common.align.model.EntityDefinition,
	 *      int)
	 */
	@Override
	public void increaseForEntity(EntityDefinition def, int count) {
		increase(def, count);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.helper.population.IPopulationUpdater#getChildren(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public Collection<? extends EntityDefinition> getChildren(EntityDefinition entityDef) {
		List<ChildContext> path = entityDef.getPropertyPath();
		Collection<? extends ChildDefinition<?>> children;

		if (path == null || path.isEmpty()) {
			// entity is a type, children are the type children
			children = entityDef.getType().getChildren();
		}
		else {
			// get parent context
			ChildContext parentContext = path.get(path.size() - 1);
			if (parentContext.getChild().asGroup() != null) {
				children = parentContext.getChild().asGroup().getDeclaredChildren();
			}
			else if (parentContext.getChild().asProperty() != null) {
				children = parentContext.getChild().asProperty().getPropertyType().getChildren();
			}
			else {
				throw new IllegalStateException("Illegal child definition type encountered");
			}
		}

		if (children == null || children.isEmpty()) {
			return Collections.emptyList();
		}

		Collection<EntityDefinition> result = new ArrayList<EntityDefinition>(children.size());
		for (ChildDefinition<?> child : children) {
			// add default child entity definition to result
			ChildContext context = new ChildContext(child);
			EntityDefinition defaultEntity = createEntity(entityDef.getType(),
					createPath(entityDef.getPropertyPath(), context), entityDef.getSchemaSpace(),
					entityDef.getFilter());
			result.add(defaultEntity);
		}

		return result;
	}

	/**
	 * Add to Population
	 * 
	 * @param instance an {@link Instance}
	 * @param entityDef an {@link EntityDefinition}
	 */
	public void addToPopulation(Instance instance, EntityDefinition entityDef) {
		if (entityDef == null) {
			TypeEntityDefinition def = getTypeEntity(instance.getDefinition(),
					SchemaSpaceID.SOURCE);
			if (def.getFilter() == null || def.getFilter().match(instance)) {
				increase(def, 1);
				populationCount.addToPopulation(instance, def);
			}
		}
		else {
			populationCount.countPopulation(entityDef, instance);
		}
	}

	private TypeEntityDefinition getTypeEntity(TypeDefinition type, SchemaSpaceID schemaSpace) {
		TypeEntityDefinition ted = new TypeEntityDefinition(type, schemaSpace, null);
		return ted;
	}

	private EntityDefinition createEntity(TypeDefinition type, List<ChildContext> path,
			SchemaSpaceID schemaSpace, Filter filter) {
		return AlignmentUtil.createEntity(type, path, schemaSpace, filter);
	}

	private static List<ChildContext> createPath(List<ChildContext> parentPath,
			ChildContext context) {
		if (parentPath == null || parentPath.isEmpty()) {
			return Collections.singletonList(context);
		}
		else {
			List<ChildContext> result = new ArrayList<ChildContext>(parentPath);
			result.add(context);
			return result;
		}
	}

	private void increase(EntityDefinition entity, int values) {

		PopulationImpl pop = population.get(entity);
		if (pop == null) {
			pop = new PopulationImpl(values != 0 ? 1 : 0, values);
			population.put(entity, pop);
		}
		else {
			if (values != 0)
				pop.increaseParents();
			pop.increaseOverall(values);
		}
	}

	/**
	 * Get Population counts
	 * 
	 * @param entityDef An {@link EntityDefinition}
	 * @return {@link Population}
	 */
	public Population getPopulation(EntityDefinition entityDef) {
		return population.get(entityDef);
	}

}
