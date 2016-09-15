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

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionDefinition;

/**
 * Interface for service resolving functions definitions.
 * 
 * @author Simon Templer
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
	FunctionDefinition<?> getFunction(String id);

	/**
	 * Get the property function w/ the given identifier.
	 * 
	 * @param id the function ID
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	@Nullable
	PropertyFunctionDefinition getPropertyFunction(String id);

	/**
	 * Get the type function w/ the given identifier.
	 * 
	 * @param id the function ID
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	@Nullable
	TypeFunctionDefinition getTypeFunction(String id);

	/**
	 * Get all type functions.
	 * 
	 * @return the collection of available type functions
	 */
	Collection<? extends TypeFunctionDefinition> getTypeFunctions();

	/**
	 * Get all property functions.
	 * 
	 * @return the collection of available property functions
	 */
	Collection<? extends PropertyFunctionDefinition> getPropertyFunctions();

	/**
	 * Get the type functions associated to the category with the given ID
	 * 
	 * @param categoryId the category ID, may be <code>null</code>
	 * @return the list of functions or an empty list
	 */
	Collection<? extends TypeFunctionDefinition> getTypeFunctions(@Nullable String categoryId);

	/**
	 * Get the property functions associated to the category with the given ID
	 * 
	 * @param categoryId the category ID, may be <code>null</code>
	 * @return the list of functions or an empty list
	 */
	Collection<? extends PropertyFunctionDefinition> getPropertyFunctions(
			@Nullable String categoryId);

}
