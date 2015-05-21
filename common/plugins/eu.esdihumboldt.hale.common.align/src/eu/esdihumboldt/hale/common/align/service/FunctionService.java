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

package eu.esdihumboldt.hale.common.align.service;

import java.util.Collection;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;

/**
 * TODO Type description
 * 
 * @author simon
 */
public interface FunctionService {

	/**
	 * Get the function w/ the given identifier.
	 * 
	 * @param id the function ID
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	@Nullable
	AbstractFunction<?> getFunction(String id);

	/**
	 * Get the property function w/ the given identifier.
	 * 
	 * @param id the function ID
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	@Nullable
	PropertyFunction getPropertyFunction(String id);

	/**
	 * Get the type function w/ the given identifier.
	 * 
	 * @param id the function ID
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	@Nullable
	TypeFunction getTypeFunction(String id);

	Collection<? extends TypeFunction> getTypeFunctions();

	Collection<? extends PropertyFunction> getPropertyFunctions();

	Collection<? extends TypeFunction> getTypeFunctions(String categoryId);

	Collection<? extends PropertyFunction> getPropertyFunctions(String categoryId);

}
