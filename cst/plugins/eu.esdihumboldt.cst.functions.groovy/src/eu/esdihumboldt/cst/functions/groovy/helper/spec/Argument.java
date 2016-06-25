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

package eu.esdihumboldt.cst.functions.groovy.helper.spec;

import javax.annotation.Nullable;

/**
 * Describes a helper function argument.
 * 
 * @author Simon Templer
 */
public interface Argument {

	/**
	 * @return the name of the argument
	 */
	public String getName();

	/**
	 * @return the description of the argument
	 */
	public String getDescription();

	/**
	 * @return the default value
	 */
	@Nullable
	public Object getDefaultValue();

}
