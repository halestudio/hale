/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.common.service.population.impl.AbstractPopulationService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;

/**
 * Service that stores information about population count.
 * @author Simon Templer
 */
public class PopulationServiceImpl extends AbstractPopulationService {
	
	private final Map<EntityDefinition, Integer> sourcePopulation = new HashMap<EntityDefinition, Integer>();
	
	private final Map<EntityDefinition, Integer> targetPopulation = new HashMap<EntityDefinition, Integer>();
	
	/**
	 * Create a population service instance.
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
				 * OrientInstanceSink, to prevent reading the whole data
				 * again from the database, just for determining the population.
				 * (If this would be done, it could be for instance in a job)
				 * An event is fired nonetheless at this point, to trigger
				 * an update.
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
	public int getPopulation(EntityDefinition entity) {
		Integer count;
		synchronized (this) {
			switch (entity.getSchemaSpace()) {
			case TARGET:
				count = targetPopulation.get(entity);
				break;
			case SOURCE:
			default:
				count = sourcePopulation.get(entity);
			}
		}
		if (count == null) {
			if (AlignmentUtil.isDefaultEntity(entity)) {
				return 0;
			}
			return UNKNOWN;
		}
		return count;
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
		} else {
			throw new IllegalArgumentException("Invalid data set specified.");
		}
		
		// count type
		EntityDefinition def = new TypeEntityDefinition(instance.getDefinition(), schemaSpace, null); 
		increase(def);
		
		addToPopulation(instance, def);
	}

	/**
	 * Count the population for the properties of the given group.
	 * @param group the group
	 * @param groupDef the group entity definition
	 */
	private void addToPopulation(Group group, EntityDefinition groupDef) {
		for (QName propertyName : group.getPropertyNames()) {
			EntityDefinition propertyDef = AlignmentUtil.getChild(groupDef, propertyName);
			
			//XXX two options to count population
			// per parent
			increase(propertyDef);
			// or per value
			// ...
			
			Object[] values = group.getProperty(propertyName);
			for (Object value : values) {
				if (value instanceof Group) {
					addToPopulation((Group) value, propertyDef);
				}
			}
		}
	}

	/**
	 * Increase the counter for the given entity.
	 * @param entity the entity
	 */
	private void increase(EntityDefinition entity) {
		synchronized (this) {
			Map<EntityDefinition, Integer> population = (entity.getSchemaSpace() == SchemaSpaceID.TARGET) ? (targetPopulation)
					: (sourcePopulation);
			
			Integer count = population.get(entity);
			if (count == null) {
				count = 1;
			}
			else {
				count++;
			}
			population.put(entity, count);
		}
	}

}
