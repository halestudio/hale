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
 * Interface for service providing Groovy script helper functions.
 * 
 * @author Simon Templer
 */
public interface HelperFunctionsService {

	/**
	 * Retrieves the children of a specific category.
	 * 
	 * @param cat the category to inspect
	 * @return the child categories and functions
	 */
	Iterable<HelperFunctionOrCategory> getChildren(Category cat);

	/**
	 * Get a child category or function.
	 * 
	 * @param cat the parent category
	 * @param name the child name
	 * @return a category, helper function or <code>null</code>
	 */
	@Nullable
	HelperFunctionOrCategory get(Category cat, String name);

}
