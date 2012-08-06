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
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractGroupPropertyDecorator;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractPropertyDecorator;

/**
 * Decorator for {@link GroupPropertyDefinition}s that overrides given 
 * constraints.
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
	public <T extends GroupPropertyConstraint> T getConstraint(
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
	protected <T extends GroupPropertyConstraint> boolean useLocalConstraint(
			Class<T> constraintType) {
		return constraints.hasConstraint(constraintType);
	}

}
