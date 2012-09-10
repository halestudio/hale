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

package eu.esdihumboldt.hale.ui.service.entity;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Listener for {@link EntityDefinitionService} events
 * 
 * @author Simon Templer
 */
public interface EntityDefinitionServiceListener {

	/**
	 * Called when a new instance context has been added.
	 * 
	 * @param contextEntity the entity definition representing the instance
	 *            context
	 */
	public void contextAdded(EntityDefinition contextEntity);

	/**
	 * Called when multiple new instance contexts have been added.
	 * 
	 * @param contextEntities the entity definitions representing the instance
	 *            contexts
	 */
	public void contextsAdded(Iterable<EntityDefinition> contextEntities);

	/**
	 * Called when an instance context has been removed.
	 * 
	 * @param contextEntity the entity definition representing the instance
	 *            context
	 */
	public void contextRemoved(EntityDefinition contextEntity);

}
