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

package eu.esdihumboldt.hale.ui.service.instance;

import java.util.Set;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceResolver;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * The {@link InstanceService} provides {@link Instance}s from different data
 * sets, e.g. the {@link DataSet#SOURCE} and {@link DataSet#TRANSFORMED} data
 * sets. It also triggers the transformation of the source to the target data
 * set.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public interface InstanceService extends InstanceResolver {

	/**
	 * The action id used for reading source data.
	 */
	public static final String ACTION_READ_SOURCEDATA = "eu.esdihumboldt.hale.io.instance.read.source";

	/**
	 * Get the instances from the given data set
	 * 
	 * @param dataset the data set
	 * @return the instance collection
	 */
	public InstanceCollection getInstances(DataSet dataset);

	/**
	 * Get the types for which instances are present in the given data set
	 * 
	 * @param dataset the data set
	 * @return the set of types for which instances are present
	 */
	public Set<TypeDefinition> getInstanceTypes(DataSet dataset);

	/**
	 * Add instances to the {@link DataSet#SOURCE} data set
	 * 
	 * @param sourceInstances the instances to add
	 */
	public void addSourceInstances(InstanceCollection sourceInstances);

	/**
	 * Set if live transformation of source data is enabled.
	 * 
	 * @param enabled if transformation is enabled
	 */
	public void setTransformationEnabled(boolean enabled);

	/**
	 * Get if live transformation of source data is enabled.
	 * 
	 * @return if live transformation is enabled
	 */
	public boolean isTransformationEnabled();

	/**
	 * This will remove all instances from the service and the corresponding
	 * resources from the project.
	 */
	public void clearInstances();

	/**
	 * Drop all instances without removing the resources from the project.
	 * Should not be undoable. Called when reloading instances.
	 */
	public void dropInstances();

	/**
	 * Adds an instance service listener
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(InstanceServiceListener listener);

	/**
	 * Removes an instance service listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(InstanceServiceListener listener);

}
