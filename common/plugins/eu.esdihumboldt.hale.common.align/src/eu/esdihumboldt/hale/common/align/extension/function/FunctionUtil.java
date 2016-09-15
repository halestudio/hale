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

package eu.esdihumboldt.hale.common.align.extension.function;

import java.util.Collection;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.service.FunctionService;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Function utility methods
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public abstract class FunctionUtil {

	/**
	 * Get the function w/ the given identifier. Falls back to static function
	 * declarations if no service is available.
	 * 
	 * @param id the function ID
	 * @param serviceProvider the service provider to retrieve the function
	 *            service for resolving the function identifier
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	public static FunctionDefinition<?> getFunction(String id,
			@Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			FunctionService fs = serviceProvider.getService(FunctionService.class);
			if (fs != null) {
				return fs.getFunction(id);
			}
		}

		AbstractFunction<?> result = null;

		result = TypeFunctionExtension.getInstance().get(id);

		if (result == null) {
			result = PropertyFunctionExtension.getInstance().get(id);
		}

		return result;
	}

	/**
	 * Get the property function w/ the given identifier. Falls back to static
	 * function declarations if no service is available.
	 * 
	 * @param id the function ID
	 * @param serviceProvider the service provider to retrieve the function
	 *            service for resolving the function identifier
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	public static PropertyFunctionDefinition getPropertyFunction(String id,
			@Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			FunctionService fs = serviceProvider.getService(FunctionService.class);
			if (fs != null) {
				return fs.getPropertyFunction(id);
			}
		}

		return PropertyFunctionExtension.getInstance().get(id);
	}

	/**
	 * Get the type function w/ the given identifier. Falls back to static
	 * function declarations if no service is available.
	 * 
	 * @param id the function ID
	 * @param serviceProvider the service provider to retrieve the function
	 *            service for resolving the function identifier
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	public static TypeFunctionDefinition getTypeFunction(String id,
			@Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			FunctionService fs = serviceProvider.getService(FunctionService.class);
			if (fs != null) {
				return fs.getTypeFunction(id);
			}
		}

		return TypeFunctionExtension.getInstance().get(id);
	}

	public static Collection<? extends TypeFunctionDefinition> getTypeFunctions(
			@Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			FunctionService fs = serviceProvider.getService(FunctionService.class);
			if (fs != null) {
				return fs.getTypeFunctions();
			}
		}

		return TypeFunctionExtension.getInstance().getElements();
	}

	public static Collection<? extends PropertyFunctionDefinition> getPropertyFunctions(
			@Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			FunctionService fs = serviceProvider.getService(FunctionService.class);
			if (fs != null) {
				return fs.getPropertyFunctions();
			}
		}

		return PropertyFunctionExtension.getInstance().getElements();
	}

	public static Collection<? extends TypeFunctionDefinition> getTypeFunctions(String categoryId,
			@Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			FunctionService fs = serviceProvider.getService(FunctionService.class);
			if (fs != null) {
				return fs.getTypeFunctions(categoryId);
			}
		}

		return TypeFunctionExtension.getInstance().getFunctions(categoryId);
	}

	public static Collection<? extends PropertyFunctionDefinition> getPropertyFunctions(
			String categoryId, @Nullable ServiceProvider serviceProvider) {
		if (serviceProvider != null) {
			FunctionService fs = serviceProvider.getService(FunctionService.class);
			if (fs != null) {
				return fs.getPropertyFunctions(categoryId);
			}
		}

		return PropertyFunctionExtension.getInstance().getFunctions(categoryId);
	}

}
