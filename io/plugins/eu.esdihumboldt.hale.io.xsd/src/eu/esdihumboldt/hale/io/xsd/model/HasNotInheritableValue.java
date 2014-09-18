/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.xsd.model;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * {@link HasValueFlag} that marks a type as having a value, but doesn't inherit
 * it to sub types.
 * 
 * @author Simon Templer
 */
@Immutable
public class HasNotInheritableValue extends HasValueFlag {

	/**
	 * Singleton instance.
	 */
	public static final HasNotInheritableValue INSTANCE = new HasNotInheritableValue();

	/**
	 * Default constructor.
	 */
	protected HasNotInheritableValue() {
		super(true);
	}

	@Override
	public boolean isInheritable() {
		return false;
	}

}
