/*
 * Copyright (c) 2018 wetransform GmbH
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

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.InstanceCollectionDecorator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * InstanceCollection decorator for collections that contain instances of only
 * one type
 * 
 * @author Florian Esser
 */
public class SingleTypeInstanceCollection extends InstanceCollectionDecorator {

	private final TypeDefinition type;

	/**
	 * @param decoratee Collection to decorate
	 * @param type Type of the contained instances
	 */
	public SingleTypeInstanceCollection(InstanceCollection decoratee, TypeDefinition type) {
		super(decoratee);

		this.type = type;
	}

	/**
	 * @return the type of the instances contained in this collection
	 */
	public TypeDefinition getType() {
		return type;
	}
}