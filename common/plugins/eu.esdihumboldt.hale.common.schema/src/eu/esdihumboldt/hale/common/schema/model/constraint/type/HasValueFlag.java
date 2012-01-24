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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a type has a direct value, apart from eventual properties, defined
 * by the schema. By default enabled for {@link TypeDefinition}s that have no 
 * properties, otherwise disabled by default.<br>
 * <br>
 * The {@link Binding} constraint defines the Java binding for the value.
 * 
 * @see Binding
 * @see AugmentedValueFlag
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class HasValueFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled has value flag
	 */
	public static final HasValueFlag ENABLED = new HasValueFlag(true);
	
	/**
	 * Disabled has value flag
	 */
	public static final HasValueFlag DISABLED = new HasValueFlag(false);
	
	/**
	 * Get the simple type flag
	 * 
	 * @param hasValue if the flag shall be enabled
	 * @return the flag
	 */
	public static HasValueFlag get(boolean hasValue) {
		return (hasValue)?(ENABLED):(DISABLED);
	}
	
	/**
	 * Creates a default simple type flag, which is disabled. If possible, 
	 * instead of creating an instance, use {@link #ENABLED} or {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public HasValueFlag() {
		this(false);
	}
	
	/**
	 * Creates a default simple type flag from a type definition. The flag is
	 * enabled if the type has no properties.
	 * @param typeDef the type definition
	 * 
	 * @see Constraint
	 */
	public HasValueFlag(TypeDefinition typeDef) {
		this(typeDef.getChildren().isEmpty());
	}
	
	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private HasValueFlag(boolean enabled) {
		super(enabled);
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// if a type has a value, its sub-type has a value too
		return true;
	}

}
