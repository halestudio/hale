/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.orient;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceDecorator;

/**
 * Instance wrapper with associated instance reference.
 * 
 * @author Simon Templer
 */
public class InstanceWithReference extends InstanceDecorator {

	private final InstanceReference reference;

	/**
	 * Create a new instance wrapper with associated instance reference
	 * 
	 * @param instance the instance
	 * @param reference the reference to the instance
	 */
	public InstanceWithReference(Instance instance, InstanceReference reference) {
		super(instance);
		this.reference = reference;
	}

	/**
	 * @return the reference to this instance
	 */
	public InstanceReference getReference() {
		return reference;
	}

}
