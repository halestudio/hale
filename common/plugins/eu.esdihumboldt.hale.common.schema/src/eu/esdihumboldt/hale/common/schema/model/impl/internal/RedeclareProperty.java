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
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractPropertyDecorator;

/**
 * Decorator for {@link PropertyDefinition}s that has a changed parent type
 * 
 * @author Simon Templer
 */
@Immutable
public class RedeclareProperty extends AbstractPropertyDecorator {

	private final DefinitionGroup declaringGroup;

	/**
	 * Create a decorator for the given property that has a changed declaring
	 * group.
	 * 
	 * @param property the property to decorate
	 * @param declaringGroup the new declaring group, may not be
	 *            <code>null</code>
	 */
	public RedeclareProperty(PropertyDefinition property, DefinitionGroup declaringGroup) {
		super(property);

		Preconditions.checkNotNull(declaringGroup);

		this.declaringGroup = declaringGroup;
	}

	/**
	 * @see AbstractPropertyDecorator#getDeclaringGroup()
	 */
	@Override
	public DefinitionGroup getDeclaringGroup() {
		return declaringGroup;
	}

}
