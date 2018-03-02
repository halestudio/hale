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

package eu.esdihumboldt.hale.common.instance.model.impl;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Identifiable;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * Instance reference based on an integer index.
 * 
 * @author Simon Templer
 */
public class IndexInstanceReference implements InstanceReference, Identifiable {

	private final DataSet dataSet;

	private final int index;

	/**
	 * Create a reference for an instance, using the given index
	 * 
	 * @param dataSet the instance data set
	 * @param index the instance index
	 */
	public IndexInstanceReference(DataSet dataSet, int index) {
		super();
		this.dataSet = dataSet;
		this.index = index;
	}

	/**
	 * @see InstanceReference#getDataSet()
	 */
	@Override
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSet == null) ? 0 : dataSet.hashCode());
		result = prime * result + index;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexInstanceReference other = (IndexInstanceReference) obj;
		if (dataSet != other.dataSet)
			return false;
		if (index != other.index)
			return false;
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Identifiable#getId()
	 */
	@Override
	public Object getId() {
		return index;
	}

}
