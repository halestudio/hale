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

package eu.esdihumboldt.cst.functions.groovy.helper;

import javax.annotation.Nullable;

/**
 * Object representing either a {@link Category} or {@link HelperFunction}.
 * 
 * @author Simon Templer
 */
public interface HelperFunctionOrCategory {

	/**
	 * @return the category or function local name (inside its parent category)
	 */
	String getName();

	/**
	 * @return the category if this object represents a category,
	 *         <code>null</code> otherwise
	 */
	@Nullable
	Category asCategory();

	/**
	 * @return the function if this object represents a function,
	 *         <code>null</code> otherwise
	 */
	@Nullable
	HelperFunction<?> asFunction();

}
