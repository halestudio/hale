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

package eu.esdihumboldt.hale.schema.model.constraints.type;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.schema.model.Constraint;
import eu.esdihumboldt.hale.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraints.AbstractFlagConstraint;

/**
 * Flags if a type is a simple type, by default enabled for 
 * {@link TypeDefinition}s that have no properties, otherwise disabled by 
 * default.
 * @author Simon Templer
 */
@Immutable
public final class SimpleFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled simple type flag
	 */
	public static final SimpleFlag ENABLED = new SimpleFlag(true);
	
	/**
	 * Disabled simple type flag
	 */
	public static final SimpleFlag DISABLED = new SimpleFlag(false);
	
	/**
	 * Get the simple type flag
	 * 
	 * @param isSimpleType if the flag shall be enabled
	 * @return the flag
	 */
	public static SimpleFlag get(boolean isSimpleType) {
		return (isSimpleType)?(ENABLED):(DISABLED);
	}
	
	/**
	 * Creates a default simple type flag, which is disabled. If possible, 
	 * instead of creating an instance, use {@link #ENABLED} or {@link #DISABLED}.
	 * @see Constraint
	 */
	public SimpleFlag() {
		this(false);
	}
	
	/**
	 * Creates a default simple type flag from a type definition. The flag is
	 * enabled if the type has no properties.
	 * @param typeDef the type definition
	 * @see Constraint
	 */
	public SimpleFlag(TypeDefinition typeDef) {
		this(typeDef.getProperties().isEmpty());
	}
	
	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private SimpleFlag(boolean enabled) {
		super(enabled);
	}

}
