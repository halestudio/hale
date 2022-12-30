/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;
import net.jcip.annotations.Immutable;

/**
 * Specifies if the order of the immediate children of the type should be
 * ignored, e.g. in case the <code>&lt;complexContent&gt;</code> is composed of
 * <code>&lt;all&gt;</code> instead of <code>&lt;sequence&gt;</code>.
 * 
 * @author Florian Esser
 */
@Immutable
@Constraint
public class IgnoreOrderFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled flag
	 */
	public static final IgnoreOrderFlag ENABLED = new IgnoreOrderFlag(true);

	/**
	 * Disabled flag
	 */
	public static final IgnoreOrderFlag DISABLED = new IgnoreOrderFlag(false);

	/**
	 * Get a flag instance
	 * 
	 * @param hasValue if the flag shall be enabled
	 * @return the flag
	 */
	public static IgnoreOrderFlag get(boolean hasValue) {
		return hasValue ? ENABLED : DISABLED;
	}

	/**
	 * Creates a default flag, which is disabled. If possible, instead of
	 * creating an instance, use {@link #ENABLED} or {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public IgnoreOrderFlag() {
		this(false);
	}

	private IgnoreOrderFlag(boolean enabled) {
		super(enabled);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// TODO This depends on the XSD version used. In XSD 1.0, extending a
		// type that uses `xs:all` is not allowed but that restriction was
		// lifted in XSD 1.1 (see https://stackoverflow.com/a/62376539).
		return true;
	}

}
