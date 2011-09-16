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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * {@link HasValueFlag} constraint that uses the constraint of the super type 
 * if possible. If no super type is present the flag is disabled.
 * @author Simon Templer
 */
public class SuperTypeHasValue extends HasValueFlag {

	private final TypeDefinition type;
	
	/**
	 * {@link HasValueFlag} constraint that uses the constraint of the super
	 * type of the given type if possible.
	 * 
	 * @param type the type to which the constraint is associated
	 */
	public SuperTypeHasValue(TypeDefinition type) {
		super();
		this.type = type;
	}

	/**
	 * @see AbstractFlagConstraint#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		TypeDefinition superType = type.getSuperType();
		
		if (superType != null) {
			return superType.getConstraint(HasValueFlag.class).isEnabled();
		}
		
		return super.isEnabled();
	}
	
}
