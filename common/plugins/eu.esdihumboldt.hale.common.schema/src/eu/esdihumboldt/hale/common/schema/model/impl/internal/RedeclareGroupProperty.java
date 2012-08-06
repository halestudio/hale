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

import com.google.common.base.Preconditions;

import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.ConstraintUtil;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractGroupPropertyDecorator;

/**
 * Decorator for {@link GroupPropertyDefinition}s that has a changed declaring
 * group.
 * @author Simon Templer
 */
@Immutable
public class RedeclareGroupProperty extends ConstraintOverrideGroupProperty {
	
	private final DefinitionGroup declaringGroup;

	/**
	 * Create a decorator for the given property that has a changed declaring
	 * group.
	 * 
	 * @param propertyGroup the property group to decorate
	 * @param declaringGroup the new declaring group, may not be 
	 *   <code>null</code>
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

	/**
	 * @see ConstraintOverrideGroupProperty#useLocalConstraint(Class)
	 */
	@Override
	protected <T extends GroupPropertyConstraint> boolean useLocalConstraint(
			Class<T> constraintType) {
		return ConstraintUtil.isParentBound(constraintType);
	}

}
