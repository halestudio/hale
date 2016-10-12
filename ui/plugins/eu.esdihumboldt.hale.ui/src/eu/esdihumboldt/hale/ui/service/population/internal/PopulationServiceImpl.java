/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.population.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.service.population.Population;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.common.service.population.impl.AbstractPopulationService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;

/**
 * Service that stores information about population count.
 * 
 * @author Simon Templer
 */
public class PopulationServiceImpl extends AbstractPopulationService {

	private static final Population NO_POPULATION = new Population() {

		@Override
		public int getParentsCount() {
			return 0;
		}

		@Override
		public int getOverallCount() {
			return 0;
		}
	};

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

	private final Map<EntityDefinition, PopulationImpl> sourcePopulation = new HashMap<EntityDefinition, PopulationImpl>();

	private final Map<EntityDefinition, PopulationImpl> targetPopulation = new HashMap<EntityDefinition, PopulationImpl>();

	private final ConditionContextEntityPopulation ccEntityPopulation = new ConditionContextEntityPopulation();

	/**
	 * Create a population service instance.
	 * 
	 * @param instanceService the instance service
	 */
	public PopulationServiceImpl(final InstanceService instanceService) {
		instanceService.addListener(new InstanceServiceAdapter() {

			@Override
			public void datasetChanged(DataSet type) {
				SchemaSpaceID ssid;
				switch (type) {
				case TRANSFORMED:
					ssid = SchemaSpaceID.TARGET;
					break;
				case SOURCE:
				default:
					ssid = SchemaSpaceID.SOURCE;
				}

				// two possibilities

				// 1 - data was added
				/*
				 * XXX this is currently handled in StoreInstancesJob and
				 * OrientInstanceSink, to prevent reading the whole data again
				 * from the database, just for determining the population. (If
				 * this would be done, it could be for instance in a job) An
				 * event is fired nonetheless at this point, to trigger an
				 * update.
				 */

				// 2 - data was cleared
				// purge the corresponding population
				InstanceCollection instances = instanceService.getInstances(type);
				if (instances.isEmpty()) {
					synchronized (PopulationServiceImpl.this) {
						switch (ssid) {
						case TARGET:
							targetPopulation.clear();
							break;
						case SOURCE:
						default:
							sourcePopulation.clear();
						}
					}
					ccEntityPopulation.resetPopulation(ssid);
				}

				firePopulationChanged(ssid);
			}

		});
	}

	/**
	 * @see PopulationService#getPopulation(EntityDefinition)
	 */
	@Override
	public Population getPopulation(EntityDefinition entity) {
		if (entity.getSchemaSpace() == null) {
			// can't determine population
			return UNKNOWN_POPULATION;
		}

		Population population;
		synchronized (this) {
			switch (entity.getSchemaSpace()) {
			case TARGET:
				population = targetPopulation.get(entity);
				break;
			case SOURCE:
			default:
				population = sourcePopulation.get(entity);
			}
		}
		if (population == null) {
			if (AlignmentUtil.isDefaultEntity(entity)) {
				return NO_POPULATION;
			}
			return ccEntityPopulation.getPopulation(entity);
		}
		return population;
	}

	/**
	 * @see PopulationService#hasPopulation(SchemaSpaceID)
	 */
	@Override
	public boolean hasPopulation(SchemaSpaceID schemaSpace) {
		synchronized (this) {
			Map<EntityDefinition, PopulationImpl> population = (schemaSpace == SchemaSpaceID.TARGET)
					? (targetPopulation) : (sourcePopulation);
			return !population.isEmpty();
		}
	}

	/**
	 * @see PopulationService#addToPopulation(Instance)
	 */
	@Override
	public void addToPopulation(Instance instance) {
		addToPopulation(instance, instance.getDataSet());
	}

	/**
	 * @see PopulationService#addToPopulation(Instance, DataSet)
	 */
	@Override
	public void addToPopulation(Instance instance, DataSet dataSet) {
		SchemaSpaceID schemaSpace;
		if (dataSet != null) {
			switch (dataSet) {
			case TRANSFORMED:
				schemaSpace = SchemaSpaceID.TARGET;
				break;
			case SOURCE:
			default:
				schemaSpace = SchemaSpaceID.SOURCE;
			}
		}
		else {
			throw new IllegalArgumentException("Invalid data set specified.");
		}

		// count type
		EntityDefinition def = new TypeEntityDefinition(instance.getDefinition(), schemaSpace,
				null);
		increase(def, 1);

		addToPopulation(instance, def);

		ccEntityPopulation.addToPopulation(instance, schemaSpace);
	}

	/**
	 * @see PopulationService#resetPopulation(DataSet)
	 */
	@Override
	public void resetPopulation(DataSet dataSet) {
		SchemaSpaceID schemaSpace;
		switch (dataSet) {
		case TRANSFORMED:
			schemaSpace = SchemaSpaceID.TARGET;
			break;
		case SOURCE:
		default:
			schemaSpace = SchemaSpaceID.SOURCE;
		}

		synchronized (this) {
			Map<EntityDefinition, PopulationImpl> population = (schemaSpace == SchemaSpaceID.TARGET)
					? (targetPopulation) : (sourcePopulation);
			population.clear();
		}

		ccEntityPopulation.resetPopulation(schemaSpace);
		// XXX rely on dataSetChanged events for update
	}

	/**
	 * Count the population for the properties of the given group.
	 * 
	 * @param group the group
	 * @param groupDef the group entity definition
	 */
	private void addToPopulation(Group group, EntityDefinition groupDef) {
		Iterable<QName> propertyNames = group.getPropertyNames();
		for (QName propertyName : propertyNames) {
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
					.getSchemaSpace() == SchemaSpaceID.TARGET) ? (targetPopulation)
							: (sourcePopulation);

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
