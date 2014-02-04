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

import java.util.HashMap;
import java.util.Map;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.ConstraintUtil;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractGroupPropertyDecorator;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractPropertyDecorator;

/**
 * Decorator for {@link GroupPropertyDefinition}s that overrides given
 * constraints.
 * 
 * @author Simon Templer
 */
@Immutable
public class ConstraintOverrideGroupProperty extends AbstractGroupPropertyDecorator {

	private final AbstractDefinition<GroupPropertyConstraint> constraints;

	/**
	 * Create a decorator for the given group property where the given
	 * constraints override the constraints of the group.
	 * 
	 * @param property the property to decorate
	 * @param constraints the overriding constraints
	 */
	public ConstraintOverrideGroupProperty(GroupPropertyDefinition property,
			GroupPropertyConstraint... constraints) {
		super(property);

		this.constraints = new AbstractDefinition<GroupPropertyConstraint>(property.getName()) {
			// empty implementation
		};

		// add constraints
		for (GroupPropertyConstraint constraint : constraints) {
			this.constraints.setConstraint(constraint);
		}
	}

	/**
	 * @see AbstractPropertyDecorator#getConstraint(Class)
	 */
	@Override
	public <T extends GroupPropertyConstraint> T getConstraint(Class<T> constraintType) {
		// return overriding constraint if present
		if (constraints.hasConstraint(constraintType)) {
			return constraints.getConstraint(constraintType);
		}

		return super.getConstraint(constraintType);
	}

	@Override
	public Iterable<GroupPropertyConstraint> getExplicitConstraints() {
		// maps constraint types to constraints
		Map<Class<?>, GroupPropertyConstraint> constraintMap = new HashMap<>();

		// add all explicit constraints from decoratee
		for (GroupPropertyConstraint constraint : super.getExplicitConstraints()) {
			constraintMap.put(ConstraintUtil.getConstraintType(constraint.getClass()), constraint);
		}

		// replace with override constraints
		for (GroupPropertyConstraint constraint : constraints.getExplicitConstraints()) {
			constraintMap.put(ConstraintUtil.getConstraintType(constraint.getClass()), constraint);
		}

		return constraintMap.values();
	}

}
