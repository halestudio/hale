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
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a type is abstract, disabled by default
 * @author Simon Templer
 */
@Immutable
@Constraint
public class AbstractFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled abstract flag
	 */
	public static final AbstractFlag ENABLED = new AbstractFlag(true);
	
	/**
	 * Disabled abstract flag
	 */
	public static final AbstractFlag DISABLED = new AbstractFlag(false);
	
	/**
	 * Get the abstract flag
	 * 
	 * @param isAbstract if the flag shall be enabled
	 * @return the flag
	 */
	public static AbstractFlag get(boolean isAbstract) {
		return (isAbstract)?(ENABLED):(DISABLED);
	}
	
	/**
	 * Creates a default abstract flag, which is disabled. If possible, instead
	 * of creating an instance, use {@link #get(boolean)}, {@link #ENABLED} or 
	 * {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public AbstractFlag() {
		this(false);
	}
	
	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private AbstractFlag(boolean enabled) {
		super(enabled);
	}
	
	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// must be set explicitly on abstract types
		return false;
	}
}
