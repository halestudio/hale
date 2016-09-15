/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.extension.function;

import eu.esdihumboldt.hale.common.core.parameter.NamedDefinition;

/**
 * Basic parameter definition.
 * 
 * @author Simon Templer
 */
public interface ParameterDefinition extends NamedDefinition {

	/**
	 * Value for {@link #getMaxOccurrence()} that represents an unbounded
	 * maximum occurrence
	 */
	public static final int UNBOUNDED = -1;

	/**
	 * @return the minimum occurrence of the parameter
	 */
	public abstract int getMinOccurrence();

	/**
	 * @return the maximum occurrence of the parameter
	 */
	public abstract int getMaxOccurrence();

}