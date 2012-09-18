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

import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractGroupPropertyDecorator;

/**
 * Decorator for {@link GroupPropertyDefinition}s that has a changed declaring
 * group.
 * 
 * @author Simon Templer
 */
@Immutable
public class RedeclareGroupProperty extends AbstractGroupPropertyDecorator {

	private final DefinitionGroup declaringGroup;

	/**
	 * Create a decorator for the given property that has a changed declaring
	 * group.
	 * 
	 * @param propertyGroup the property group to decorate
	 * @param declaringGroup the new declaring group, may not be
	 *            <code>null</code>
	 */
	public RedeclareGroupProperty(GroupPropertyDefinition propertyGroup,
			DefinitionGroup declaringGroup) {
		super(propertyGroup);

		Preconditions.checkNotNull(declaringGroup);

		this.declaringGroup = declaringGroup;
	}

	/**
	 * @see AbstractGroupPropertyDecorator#getDeclaringGroup()
	 */
	@Override
	public DefinitionGroup getDeclaringGroup() {
		return declaringGroup;
	}

}
