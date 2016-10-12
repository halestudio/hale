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

package eu.esdihumboldt.hale.ui.service.population.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.service.population.Population;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * To count population for entities which have been created by condition
 * context.
 * 
 * @author Arun
 */
public class ConditionContextEntityPopulation {

	private boolean isUpdated;

	private static final Population UNKNOWN_POPULATION = new Population() {

		@Override
		public int getParentsCount() {
			return Population.UNKNOWN;
		}

		@Override
		public int getOverallCount() {
			return Population.UNKNOWN;
		}
	};

	private final Map<EntityDefinition, PopulationImpl> sourceCCEntitiesPopulation = new HashMap<EntityDefinition, PopulationImpl>();

	private final Map<EntityDefinition, PopulationImpl> targetCCEntitiesPopulation = new HashMap<EntityDefinition, PopulationImpl>();

	/**
	 * @return the isUpdated
	 */
	public boolean isUpdated() {
		return isUpdated;
	}

	/**
	 * @param isUpdated the isUpdated to set
	 */
	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	/**
	 * clear the population count of given SchemaSpace
	 * 
	 * @param schemaSpace A Schema space
	 */
	public void resetPopulation(SchemaSpaceID schemaSpace) {
		synchronized (this) {
			Map<EntityDefinition, PopulationImpl> population = (schemaSpace == SchemaSpaceID.TARGET)
					? (targetCCEntitiesPopulation) : (sourceCCEntitiesPopulation);
			population.clear();
		}
	}

	/**
	 * Get the population count for the given entity.
	 * 
	 * @param entity the entity
	 * @return the population
	 */
	public Population getPopulation(EntityDefinition entity) {
		if (entity.getSchemaSpace() == null) {
			// can't determine population
			return UNKNOWN_POPULATION;
		}

		Population population = getPopulationFromMap(entity);
		if (population == null) {

			if (entity.getFilter() == null)
				return UNKNOWN_POPULATION;

			InstanceService instanceService = PlatformUI.getWorkbench()
					.getService(InstanceService.class);

			// determine data set
			DataSet dataSet = DataSet.forSchemaSpace(entity.getSchemaSpace());

			InstanceCollection instances = instanceService.getInstances(dataSet);
			if (!instances.isEmpty()) {
				// go through instances to determine occurring values
				return updateAndGetPopulation(entity, instances);
			}
		}
		return population != null ? population : UNKNOWN_POPULATION;
	}

	private Population getPopulationFromMap(EntityDefinition entity) {
		synchronized (this) {
			switch (entity.getSchemaSpace()) {
			case TARGET:
				return targetCCEntitiesPopulation.get(entity);
			case SOURCE:
			default:
				return sourceCCEntitiesPopulation.get(entity);
			}
		}
	}

	/**
	 * Add an instance to the population, explicitly specifying the associated
	 * SchemaSpace
	 * 
	 * @param instance the instance
	 * @param schemaSpace the schemaSpace the instance belong to
	 */
	public void addToPopulation(Instance instance, SchemaSpaceID schemaSpace) {
		// loop to each condition context entity and check newly added instance
		// is match to any filter or not?
		synchronized (this) {
			Map<EntityDefinition, PopulationImpl> population = (schemaSpace == SchemaSpaceID.TARGET)
					? (targetCCEntitiesPopulation) : (sourceCCEntitiesPopulation);

			if (population.isEmpty())
				return;

			for (EntityDefinition def : population.keySet()) {
				if (def instanceof TypeEntityDefinition
						&& def.getDefinition().getDisplayName()
								.equals(instance.getDefinition().getDisplayName())
						&& def.getFilter().match(instance)) {
					increase(def, 1);
					addToPopulation(instance, def);
				}
			}
		}
	}

	private Population updateAndGetPopulation(EntityDefinition ccEntityDefinition,
			InstanceCollection instanceCollection) {

		// only select instances of the correct type
		InstanceCollection instances = instanceCollection
				.select(new TypeFilter(ccEntityDefinition.getType()));
		// and apply an eventual filter
		if (ccEntityDefinition.getFilter() != null) {
			instances = instances.select(ccEntityDefinition.getFilter());
		}
		if (!instances.isEmpty()) {
			// count instances
			ResourceIterator<Instance> it = instances.iterator();
			try {
				while (it.hasNext()) {
					increase(ccEntityDefinition, 1);
					addToPopulation(it.next(), ccEntityDefinition);
				}
			} finally {
				it.close();
			}

			Population pop = getPopulationFromMap(ccEntityDefinition);
			return pop;
		}

		return UNKNOWN_POPULATION;

	}

	/**
	 * Count the population for the properties of the given group.
	 * 
	 * @param group the group
	 * @param groupDef the group entity definition
	 */
	private void addToPopulation(Group group, EntityDefinition groupDef) {
		for (QName propertyName : group.getPropertyNames()) {
			EntityDefinition propertyDef = AlignmentUtil.getChild(groupDef, propertyName);

			if (propertyDef != null) {
				Object[] values = group.getProperty(propertyName);

				increase(propertyDef, values.length);

				for (Object value : values) {
					if (value instanceof Group) {
						addToPopulation((Group) value, propertyDef);
					}
				}
			}
		}
	}

	/**
	 * Increase the counter for the given entity per parent.
	 * 
	 * @param entity the entity
	 * @param values number of values
	 */
	private void increase(EntityDefinition entity, int values) {
		synchronized (this) {
			Map<EntityDefinition, PopulationImpl> population = (entity
					.getSchemaSpace() == SchemaSpaceID.TARGET) ? (targetCCEntitiesPopulation)
							: (sourceCCEntitiesPopulation);

			PopulationImpl pop = population.get(entity);
			if (pop == null) {
				pop = new PopulationImpl(1, values);
				population.put(entity, pop);
			}
			else {
				pop.increaseParents();
				pop.increaseOverall(values);
			}
		}
	}
}
