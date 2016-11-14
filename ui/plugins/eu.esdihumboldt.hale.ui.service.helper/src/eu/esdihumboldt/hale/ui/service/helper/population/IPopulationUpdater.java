package eu.esdihumboldt.hale.ui.service.helper.population;


import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Notifier for Population count helper class
 * @author Arun
 *
 */
public interface IPopulationUpdater {

	/**
	 * Increase count for given entity
	 * @param def A {@link EntityDefinition}
	 * @param count counts for given EntityDefinition
	 */
	public void increaseForEntity(EntityDefinition def, int count);

	/**
	 * Get children of entity definition
	 * @param entityDef A {@link EntityDefinition}
	 * @return All the children including contexts.
	 */
	public Collection<? extends EntityDefinition> getChildren(EntityDefinition entityDef);
	
}
