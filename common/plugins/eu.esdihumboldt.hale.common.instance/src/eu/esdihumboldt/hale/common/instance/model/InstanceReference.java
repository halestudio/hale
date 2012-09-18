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

package eu.esdihumboldt.hale.common.instance.model;

/**
 * Represents a reference to an instance. Implementations must implement
 * {@link #hashCode()} and {@link #equals(Object)} to uniquely identify an
 * instance.
 * 
 * @author Simon Templer
 */
public interface InstanceReference {

	// XXX move getDataSet to InstanceReference interface applicable for
	// InstanceService?

	/**
	 * Get the data set the instance is associated to.
	 * 
	 * @return the instance data set, <code>null</code> if not set
	 */
	public DataSet getDataSet();

}
