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

package eu.esdihumboldt.hale.common.instance.model.impl;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * Instance reference based on an integer index.
 * @author Simon Templer
 */
public class IndexInstanceReference implements InstanceReference {

	private final DataSet dataSet;
	
	private final int index;

	/**
	 * Create a reference for an instance, using the given index
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

}
