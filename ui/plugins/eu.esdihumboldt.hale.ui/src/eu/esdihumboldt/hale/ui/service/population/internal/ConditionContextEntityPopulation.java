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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
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
	 * Job determining the occurring values for a specific property entity.
	 */
	private class PopulationCountJob extends Job {

		private final EntityDefinition ccEntityDefinition;
		private final InstanceCollection instances;

		/**
		 * Create a Job to get the population of given {@link EntityDefinition}
		 * 
		 * @param ccEntityDefinition the condition context entity definition
		 * @param instances the instances to test
		 */
		public PopulationCountJob(EntityDefinition ccEntityDefinition,
				InstanceCollection instances) {
			super("Determinining counts for ConditionContext values");
			this.ccEntityDefinition = ccEntityDefinition;
			this.instances = instances;

			setUser(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {

			String taskName = "Check instances for condition";

			// only select instances of the correct type
			InstanceCollection instances = this.instances
					.select(new TypeFilter(ccEntityDefinition.getType()));
			// and apply an eventual filter
			if (ccEntityDefinition.getFilter() != null) {
				instances = instances.select(ccEntityDefinition.getFilter());
			}

			boolean isKnownSize = instances.hasSize();

			if (isKnownSize) {
				monitor.beginTask(taskName, instances.size());
			}
			else {
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
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
			}

			monitor.done();

			return Status.OK_STATUS;
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
				Job job = new PopulationCountJob(entity, instances);
				job.schedule();
				try {
					job.join();
				} catch (InterruptedException e) {
					//
				}
			}

			population = getPopulationFromMap(entity);
			return population != null ? population : UNKNOWN_POPULATION;
		}
		return population;
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

	public void inValidate() {

	}

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

}
