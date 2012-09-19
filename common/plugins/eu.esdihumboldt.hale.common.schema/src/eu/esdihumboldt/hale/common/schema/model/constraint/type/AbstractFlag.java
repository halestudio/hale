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
 * Flags if a type is abstract, disabled by default
 * 
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
		return (isAbstract) ? (ENABLED) : (DISABLED);
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
