/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.util.Objects;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;

/**
 * InstanceReference decorator class.
 * 
 * @author Florian Esser
 */
public class InstanceReferenceDecorator implements InstanceReference {

	private final InstanceReference reference;

	/**
	 * Constructs the decorator with the given reference.
	 * 
	 * @param reference the reference to decorate
	 */
	public InstanceReferenceDecorator(InstanceReference reference) {
		this.reference = Objects.requireNonNull(reference);
	}

	/**
	 * Returns the original reference.
	 * 
	 * @return the original reference
	 */
	public InstanceReference getOriginalReference() {
		return reference;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.InstanceReference#getDataSet()
	 */
	@Override
	public DataSet getDataSet() {
		return reference.getDataSet();
	}

}
