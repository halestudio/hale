/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.schema.model.impl.internal;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractPropertyDecorator;

/**
 * Decorator for {@link PropertyDefinition}s that overrides given constraints.
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
	public <T extends PropertyConstraint> T getConstraint(
			Class<T> constraintType) {
		// return overriding constraint if present
		if (useLocalConstraint(constraintType)) {
			return constraints.getConstraint(constraintType);
		}
		
		return super.getConstraint(constraintType);
	}
	
	/**
	 * Determines if a local constraint should be used, or if the constraint
	 * should be retrieved from the decoratee.
	 * @param constraintType the constraint type
	 * @return if a local constraint should be used
	 */
	protected <T extends PropertyConstraint> boolean useLocalConstraint(
			Class<T> constraintType) {
		return constraints.hasConstraint(constraintType);
	}

}
