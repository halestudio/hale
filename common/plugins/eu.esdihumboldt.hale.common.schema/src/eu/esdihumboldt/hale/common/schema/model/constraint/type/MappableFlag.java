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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a type is mappable, i.e. that it is a valid source or target for a
 * retype. Disabled by default.
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public class MappableFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled mappable flag
	 */
	public static final MappableFlag ENABLED = new MappableFlag(true);

	/**
	 * Disabled mappable flag
	 */
	public static final MappableFlag DISABLED = new MappableFlag(false);

	/**
	 * Get the mappable flag
	 * 
	 * @param isMappable if the flag shall be enabled
	 * @return the flag
	 */
	public static MappableFlag get(boolean isMappable) {
		return (isMappable) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default mappable flag, which is disabled. If possible, instead
	 * of creating an instance, use {@link #get(boolean)}, {@link #ENABLED} or
	 * {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public MappableFlag() {
		// disabled by default because of simple types etc.
		this(false);
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private MappableFlag(boolean enabled) {
		super(enabled);
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// must be set explicitly
		return true;
	}

}
