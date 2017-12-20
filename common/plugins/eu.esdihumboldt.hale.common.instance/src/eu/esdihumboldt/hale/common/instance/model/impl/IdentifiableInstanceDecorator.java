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

import eu.esdihumboldt.hale.common.instance.model.Identifiable;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Instance decorator that supports {@link #getId()}
 * 
 * @author Florian Esser
 */
public class IdentifiableInstanceDecorator extends InstanceDecorator implements Identifiable {

	/**
	 * Constructs the decorator with the given instance.
	 * 
	 * @param instance the instance to decorate
	 */
	public IdentifiableInstanceDecorator(Instance instance) {
		super(instance);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Identifiable#getId()
	 */
	@Override
	public Object getId() {
		return Identifiable.getId(getOriginalInstance());
	}

}
