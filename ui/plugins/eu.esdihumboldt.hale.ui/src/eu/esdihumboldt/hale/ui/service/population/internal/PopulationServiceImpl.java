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
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;

/**
 * Service that stores information about population count.
 * @author Simon Templer
 */
public class PopulationServiceImpl implements PopulationService {
	
	private final Map<EntityDefinition, Integer> population = new HashMap<EntityDefinition, Integer>();
	
	/**
	 * @see PopulationService#getPopulation(EntityDefinition)
	 */
	@Override
	public int getPopulation(EntityDefinition entity) {
		Integer count = population.get(entity);
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
		SchemaSpaceID schemaSpace;
		if (instance.getDataSet() != null) {
			switch (instance.getDataSet()) {
			case TRANSFORMED:
				schemaSpace = SchemaSpaceID.TARGET;
				break;
			case SOURCE:
			default:
				schemaSpace = SchemaSpaceID.SOURCE;
			}
		} else {
			schemaSpace = SchemaSpaceID.SOURCE; // assuming that for transformed instances always the data set is correctly set
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
