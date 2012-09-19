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

package eu.esdihumboldt.hale.ui.common.service.population;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Service for tracking instance population.
 * 
 * @author Simon Templer
 */
public interface PopulationService {

	/**
	 * Get the population count for the given entity.
	 * 
	 * @param entity the entity
	 * @return the population
	 */
	public Population getPopulation(EntityDefinition entity);

	/**
	 * Add an instance to the population.
	 * 
	 * @param instance the instance, it has to have a valid associated data set
	 */
	public void addToPopulation(Instance instance);

	/**
	 * Add an instance to the population, explicitly specifying the associated
	 * data set.
	 * 
	 * @param instance the instance
	 * @param dataSet the data set the instance belongs to
	 */
	public void addToPopulation(Instance instance, DataSet dataSet);

	/**
	 * Reset the population of the given data set
	 * 
	 * @param dataSet the data set
	 */
	public void resetPopulation(DataSet dataSet);

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
	 * 
	 * @param listener the listener
	 */
	public void addListener(PopulationListener listener);

	/**
	 * Removes a listener for population events.
	 * 
	 * @param listener the listener
	 */
	public void removeListener(PopulationListener listener);

	/**
	 * Determines if there is any population for the given schema space.
	 * 
	 * @param schemaSpace the schema space
	 * @return if there is any population for the schema space
	 */
	public boolean hasPopulation(SchemaSpaceID schemaSpace);

}
