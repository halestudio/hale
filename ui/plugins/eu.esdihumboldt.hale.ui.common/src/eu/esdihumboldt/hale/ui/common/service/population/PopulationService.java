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

package eu.esdihumboldt.hale.ui.common.service.population;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Service for tracking instance population.
 * @author Simon Templer
 */
public interface PopulationService {
	
	/**
	 * Constant representing unknown population count.
	 */
	public static final int UNKNOWN = -1;

	/**
	 * Get the population count for the given entity.
	 * @param entity the entity
	 * @return the population count or {@link #UNKNOWN}
	 */
	public int getPopulation(EntityDefinition entity);
	
	/**
	 * Add an instance to the population.
	 * @param instance the instance, it has to have a valid associated data set
	 */
	public void addToPopulation(Instance instance);
	
	/**
	 * Add an instance to the population, explicitly specifying the associated
	 * data set.
	 * @param instance the instance
	 * @param dataSet the data set the instance belongs to
	 */
	public void addToPopulation(Instance instance, DataSet dataSet);
	
//	/**
//	 * Increase the population count for the given entity by one.
//	 * @param entity the entity
//	 */
//	public void increasePopulation(EntityDefinition entity);
//	
//	/**
//	 * Increase the population count for the given entity by the given number.
//	 * @param entity the entity
//	 * @param number the amount to increase the population count
//	 */
//	public void increasePopulation(EntityDefinition entity, int number);
	
	/**
	 * Adds a listener for population events.
	 * @param listener the listener
	 */
	public void addListener(PopulationListener listener);
	
	/**
	 * Removes a listener for population events.
	 * @param listener the listener
	 */
	public void removeListener(PopulationListener listener);

	/**
	 * Determines if there is any population for the given schema space.
	 * @param schemaSpace the schema space
	 * @return if there is any population for the schema space
	 */
	public boolean hasPopulation(SchemaSpaceID schemaSpace);

}
