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

package eu.esdihumboldt.hale.common.instance.model;

import eu.esdihumboldt.hale.common.instance.model.impl.InstanceReferenceDecorator;

/**
 * {@link InstanceReference} that stores the ID of the referenced instance
 * 
 * @author Florian Esser
 */
public class IdentifiableInstanceReference extends InstanceReferenceDecorator
		implements Identifiable {

	private final Object id;

	/**
	 * Create the identifiable instance reference
	 * 
	 * @param reference Original reference
	 * @param id Identifier
	 */
	public IdentifiableInstanceReference(InstanceReference reference, Object id) {
		super(reference);
		this.id = id;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Identifiable#getId()
	 */
	@Override
	public Object getId() {
		return this.id;
	}

}
