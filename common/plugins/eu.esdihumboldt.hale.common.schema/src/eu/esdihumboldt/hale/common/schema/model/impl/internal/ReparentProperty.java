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

package eu.esdihumboldt.hale.common.schema.model.impl.internal;

import net.jcip.annotations.Immutable;

import com.google.common.base.Preconditions;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractPropertyDecorator;

/**
 * Decorator for {@link PropertyDefinition}s that has a changed parent type
 * 
 * @author Simon Templer
 */
@Immutable
public class ReparentProperty extends AbstractPropertyDecorator {

	private final TypeDefinition parent;

	/**
	 * Create a decorator for the given property that has a changed parent type
	 * 
	 * @param property the property to decorate
	 * @param newParent the new parent type, may not be <code>null</code>
	 */
	public ReparentProperty(PropertyDefinition property, TypeDefinition newParent) {
		super(property);

		Preconditions.checkNotNull(newParent);

		this.parent = newParent;
	}

	/**
	 * @see AbstractPropertyDecorator#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return parent.getIdentifier() + "/" + getName().getLocalPart(); //$NON-NLS-1$
	}

	/**
	 * @see AbstractPropertyDecorator#getParentType()
	 */
	@Override
	public TypeDefinition getParentType() {
		return parent;
	}

}
