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
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * An instance reference that contains the instance. Two pseudo references are
 * equal if the the contained instance object is the same.
 * 
 * @author Simon Templer
 */
public class PseudoInstanceReference implements InstanceReference {

	private final Instance instance;

	/**
	 * Create a pseudo instance reference.
	 * 
	 * @param instance the instance
	 */
	public PseudoInstanceReference(Instance instance) {
		super();
		this.instance = instance;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceReference#getDataSet()
	 */
	@Override
	public DataSet getDataSet() {
		return instance.getDataSet();
	}

	/**
	 * @return the instance
	 */
	public Instance getInstance() {
		return instance;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return instance.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PseudoInstanceReference)) {
			return false;
		}
		return instance == ((PseudoInstanceReference) obj).instance;
	}

}
