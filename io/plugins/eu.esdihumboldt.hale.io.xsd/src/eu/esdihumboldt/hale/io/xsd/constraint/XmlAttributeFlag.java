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
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a property is represented by a XML attribute, disabled by default
 * 
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public final class XmlAttributeFlag extends AbstractFlagConstraint implements PropertyConstraint {

	/**
	 * Enabled XML attribute flag
	 */
	public static final XmlAttributeFlag ENABLED = new XmlAttributeFlag(true);

	/**
	 * Disabled XML attribute flag
	 */
	public static final XmlAttributeFlag DISABLED = new XmlAttributeFlag(false);

	/**
	 * Get the XML attribute flag
	 * 
	 * @param isAttribute if the flag shall be enabled
	 * @return the flag
	 */
	public static XmlAttributeFlag get(boolean isAttribute) {
		return (isAttribute) ? (ENABLED) : (DISABLED);
	}

	/**
	 * Creates a default XML attribute flag, which is disabled. If possible,
	 * instead of creating an instance, use {@link #get(boolean)},
	 * {@link #ENABLED} or {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public XmlAttributeFlag() {
		this(false);
	}

	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private XmlAttributeFlag(boolean enabled) {
		super(enabled);
	}

}
