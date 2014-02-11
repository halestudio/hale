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
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.ConstraintUtil;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractPropertyDecorator;

/**
 * Decorator for {@link PropertyDefinition}s that overrides given constraints.
 * 
 * @author Simon Templer
 */
@Immutable
public class ConstraintOverrideProperty extends AbstractPropertyDecorator {

	private final AbstractDefinition<PropertyConstraint> constraints;

	/**
	 * Create a decorator for the given property where the given constraints
	 * override the constraints of the property.
	 * 
	 * @param property the property to decorate
	 * @param constraints the overriding constraints
	 */
	public ConstraintOverrideProperty(PropertyDefinition property,
			PropertyConstraint... constraints) {
		super(property);

		this.constraints = new AbstractDefinition<PropertyConstraint>(property.getName()) {
			// empty implementation
		};

		// add constraints
		for (PropertyConstraint constraint : constraints) {
			this.constraints.setConstraint(constraint);
		}
	}

	/**
	 * @see AbstractPropertyDecorator#getConstraint(Class)
	 */
	@Override
	public <T extends PropertyConstraint> T getConstraint(Class<T> constraintType) {
		// return overriding constraint if present
		if (constraints.hasConstraint(constraintType)) {
			return constraints.getConstraint(constraintType);
		}

		return super.getConstraint(constraintType);
	}

	@Override
	public Iterable<PropertyConstraint> getExplicitConstraints() {
		// maps constraint types to constraints
		Map<Class<?>, PropertyConstraint> constraintMap = new HashMap<>();

		// add all explicit constraints from decoratee
		for (PropertyConstraint constraint : super.getExplicitConstraints()) {
			constraintMap.put(ConstraintUtil.getConstraintType(constraint.getClass()), constraint);
		}

		// replace with override constraints
		for (PropertyConstraint constraint : constraints.getExplicitConstraints()) {
			constraintMap.put(ConstraintUtil.getConstraintType(constraint.getClass()), constraint);
		}

		return constraintMap.values();
	}

}
