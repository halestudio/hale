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

package eu.esdihumboldt.hale.common.instance.index;

import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.impl.InstanceReferenceDecorator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Adds a {@link TypeDefinition} to an {@link InstanceReference}
 * 
 * @author Florian Esser
 */
public class TypedInstanceReference extends InstanceReferenceDecorator implements Typed {

	private final TypeDefinition definition;

	/**
	 * Crates a typed instanced reference
	 * 
	 * @param reference Reference to decorate
	 * @param type Type definition to associate
	 */
	public TypedInstanceReference(InstanceReference reference, TypeDefinition type) {
		super(reference);

		this.definition = type;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.Typed#getDefinition()
	 */
	@Override
	public TypeDefinition getDefinition() {
		return definition;
	}

}
