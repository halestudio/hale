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

import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractGroupPropertyDecorator;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractPropertyDecorator;

/**
 * Decorator for {@link GroupPropertyDefinition}s that has a changed parent type
 * 
 * @author Simon Templer
 */
@Immutable
public class ReparentGroupProperty extends AbstractGroupPropertyDecorator {

	private final TypeDefinition parent;

	/**
	 * Create a decorator for the given property that has a changed parent type
	 * 
	 * @param propertyGroup the property group to decorate
	 * @param newParent the new parent type, may not be <code>null</code>
	 */
	public ReparentGroupProperty(GroupPropertyDefinition propertyGroup, TypeDefinition newParent) {
		super(propertyGroup);

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
