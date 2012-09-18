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
