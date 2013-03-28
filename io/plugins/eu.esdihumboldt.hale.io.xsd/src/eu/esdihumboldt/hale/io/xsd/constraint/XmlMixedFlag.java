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

package eu.esdihumboldt.hale.io.xsd.constraint;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a type is a mixed XML type and therefore can contain text, elements
 * and attributes, disabled by default.
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public final class XmlMixedFlag extends AbstractFlagConstraint implements TypeConstraint {

	/**
	 * Enabled XML mixed type flag
	 */
	public static final XmlMixedFlag ENABLED = new XmlMixedFlag(true);

	/**
	 * Disabled XML mixed type flag
	 */
	public static final XmlMixedFlag DISABLED = new XmlMixedFlag(false);

	/**
	 * Get the XML mixed flag
	 * 
	 * @param isMixed if the flag shall be enabled
	 * @return the flag
	 */
	public static XmlMixedFlag get(boolean isMixed) {
		return (isMixed) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default XML mixed type flag, which is disabled. If possible,
	 * instead of creating an instance, use {@link #get(boolean)},
	 * {@link #ENABLED} or {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public XmlMixedFlag() {
		this(false);
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private XmlMixedFlag(boolean enabled) {
		super(enabled);
	}

	@Override
	public boolean isInheritable() {
		// mixed is not inherited
		return false;
	}

}
