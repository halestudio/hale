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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
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
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.common.service.population.impl.AbstractPopulationService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionServiceListener;
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

	private final EntityDefinitionService entityDefinitionService;

	/**
	 * Create a population service instance.
	 * 
	 * @param instanceService the instance service
	 */
	public PopulationServiceImpl(final InstanceService instanceService) {

		entityDefinitionService = PlatformUI.getWorkbench()
				.getService(EntityDefinitionService.class);

		entityDefinitionService.addListener(new EntityDefinitionServiceListener() {

			@Override
			public void contextsAdded(Iterable<EntityDefinition> contextEntities) {

				for (EntityDefinition ed : contextEntities) {
					contextAdded(ed);
				}

			}

			@Override
			public void contextRemoved(EntityDefinition contextEntity) {
				// Not needed
			}

			@Override
			public void contextAdded(EntityDefinition contextEntity) {
				// go through instances to determine occurring values
				// if population is already counted before for given Entity,
				// then no need to count it again
				Map<EntityDefinition, PopulationImpl> population = (contextEntity
						.getSchemaSpace() == SchemaSpaceID.TARGET) ? (targetPopulation)
								: (sourcePopulation);
				if (population.get(contextEntity) == null) {
					Job job = new PopulationCountJob(contextEntity);
					job.schedule();
				}
			}
		});

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
			return UNKNOWN_POPULATION;
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

		// count for each Type definitions of instance type
		Collection<? extends TypeEntityDefinition> typeDefinitions = entityDefinitionService
				.getTypeEntities(instance.getDefinition(), schemaSpace);

		for (TypeEntityDefinition def : typeDefinitions) {
			if (def.getFilter() == null || def.getFilter().match(instance)) {
				increase(def, 1);
				addToPopulation(instance, def, def.getPropertyPath());
			}
		}
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

		// XXX rely on dataSetChanged events for update
	}

	/**
	 * Count the population for the properties of the given group.
	 * 
	 * @param group the group
	 * @param groupDef the group entity definition
	 * @param path A Child Context path
	 */
	private void addToPopulation(Group group, EntityDefinition groupDef, List<ChildContext> path) {
		Iterable<? extends EntityDefinition> children = entityDefinitionService
				.getChildren(groupDef);
		if (children != null && children.iterator().hasNext()) {
			for (EntityDefinition def : children) {
				if (groupDef instanceof TypeEntityDefinition)
					path = def.getPropertyPath();
				evaluateContext(group, def, path);
			}
		}
		else {
			evaluateContext(group, groupDef, path);
		}
	}

	private void evaluateContext(Group group, EntityDefinition groupDef, List<ChildContext> path) {

		if (path == null || path.isEmpty()) {
			// group or instance at end of path
			increase(groupDef, 1);
		}
		else {
			ChildContext context = path.get(0);
			List<ChildContext> subPath = null;
			if (path.size() > 0) {
				subPath = path.subList(1, path.size());
			}
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
						addToPopulation((Group) value, groupDef, subPath);
					}
				}
			}
			else {
				increase(groupDef, 0);
			}

		}

	}

	private void addNoneToPopulation(EntityDefinition groupDef, List<ChildContext> path) {
		Iterable<? extends EntityDefinition> children = entityDefinitionService
				.getChildren(groupDef);
		if (children != null && children.iterator().hasNext()) {
			for (EntityDefinition def : children) {
				if (groupDef instanceof TypeEntityDefinition)
					path = def.getPropertyPath();
				addNoneToChildren(def, path);
			}
		}
		else {
			addNoneToChildren(groupDef, path);
		}
	}

	private void addNoneToChildren(EntityDefinition groupDef, List<ChildContext> path) {
		if (path == null || path.isEmpty()) {
			increase(groupDef, 0);
		}
		else {
			List<ChildContext> subPath = null;
			if (path.size() > 0) {
				subPath = path.subList(1, path.size());
			}
			increase(groupDef, 0);
			addNoneToPopulation(groupDef, subPath);
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
				pop = new PopulationImpl(values != 0 ? 1 : 0, values);
				population.put(entity, pop);
			}
			else {
				if (values != 0)
					pop.increaseParents();
				pop.increaseOverall(values);
			}

		}
	}

	/**
	 * Job determining the occurring values for a specific property entity.
	 */
	private class PopulationCountJob extends Job {

		private final EntityDefinition ccEntityDefinition;
		private InstanceCollection instanceCollection;

		/**
		 * Create a Job to get the population of given {@link EntityDefinition}
		 * 
		 * @param ccEntityDefinition the condition context entity definition
		 */
		public PopulationCountJob(EntityDefinition ccEntityDefinition) {
			this(ccEntityDefinition, null);
		}

		/**
		 * Create a Job to get the population of given {@link EntityDefinition}
		 * 
		 * @param ccEntityDefinition the condition context entity definition
		 * @param instanceCollection an instance collection
		 */
		public PopulationCountJob(EntityDefinition ccEntityDefinition,
				InstanceCollection instanceCollection) {
			super("Determinining count for contexts");
			this.ccEntityDefinition = ccEntityDefinition;
			this.instanceCollection = instanceCollection;

			setUser(false);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {

			String taskName = "Check instances for condition";

			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);

			if (this.instanceCollection == null) {
				InstanceService instanceService = PlatformUI.getWorkbench()
						.getService(InstanceService.class);

				// determine data set
				DataSet dataSet = DataSet.forSchemaSpace(ccEntityDefinition.getSchemaSpace());

				this.instanceCollection = instanceService.getInstances(dataSet);
			}

			if (!instanceCollection.isEmpty()) {
				// only select instances of the correct type
				InstanceCollection instances = instanceCollection
						.select(new TypeFilter(ccEntityDefinition.getType()));
				// and apply an eventual filter
				if (ccEntityDefinition.getFilter() != null) {
					instances = instances.select(ccEntityDefinition.getFilter());
				}

				if (instances.isEmpty()) {
					Map<EntityDefinition, PopulationImpl> population = (ccEntityDefinition
							.getSchemaSpace() == SchemaSpaceID.TARGET) ? (targetPopulation)
									: (sourcePopulation);

					PopulationImpl pop = population.get(ccEntityDefinition);
					if (pop == null) {
						population.put(ccEntityDefinition, new PopulationImpl(0, 0));
					}
					addNoneToPopulation(ccEntityDefinition, ccEntityDefinition.getPropertyPath());
				}
				else {
					// count instances
					ResourceIterator<Instance> it = instances.iterator();
					try {
						while (it.hasNext()) {
							List<ChildContext> path = ccEntityDefinition.getPropertyPath();
							Instance instance = it.next();
							if (path == null || path.isEmpty()) {
								if (ccEntityDefinition.getFilter() == null
										|| ccEntityDefinition.getFilter().match(instance)) {
									PopulationServiceImpl.this.increase(ccEntityDefinition, 1);
									PopulationServiceImpl.this.addToPopulation(instance,
											ccEntityDefinition, path);
								}
							}
							else {
								PopulationServiceImpl.this.evaluateContext(instance,
										ccEntityDefinition, path);
							}
						}
					} finally {
						it.close();
					}
				}
			}
			monitor.done();
			firePopulationChanged(ccEntityDefinition.getSchemaSpace());
			return Status.OK_STATUS;
		}

	}
}
