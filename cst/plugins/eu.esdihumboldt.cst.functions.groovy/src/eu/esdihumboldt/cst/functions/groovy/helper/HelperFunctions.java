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

import eu.esdihumboldt.hale.common.core.HalePlatform;
import groovy.lang.GroovyObjectSupport;

import java.text.MessageFormat;

import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Accessor for Groovy script helper functions and categories.
 * 
 * @author Simon Templer
 */
public class HelperFunctions extends GroovyObjectSupport {

	private final Category category;

	private final HelperFunctionsService functions;

	/**
	 * Creates a default {@link HelperFunctions} object starting at the root
	 * category and using the OSGi published {@link HelperFunctionsService}.
	 * 
	 * @return the helper functions accessor
	 */
	public static HelperFunctions createDefault() {
		HelperFunctionsService functions = HalePlatform.getService(HelperFunctionsService.class);
		return new HelperFunctions(functions, Category.ROOT);
	}

	/**
	 * Constructor.
	 * 
	 * @param functions the helper functions service
	 * @param category the start category
	 */
	public HelperFunctions(HelperFunctionsService functions, Category category) {
		super();
		this.functions = functions;
		this.category = category;
	}

	@Override
	public Object getProperty(String name) {
		HelperFunctionOrCategory obj = functions.get(category, name);
		if (obj == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					"No function or category named {0}.{1} exists.", category, name));
		}
		Category cat = obj.asCategory();
		if (cat != null) {
			return new HelperFunctions(functions, cat);
		}
		else {
			return obj.asFunction();
		}
	}

	@Override
	public Object invokeMethod(String name, Object args) {
		Object obj = getProperty(name);
		if (obj instanceof HelperFunction) {
			HelperFunction<?> function = (HelperFunction<?>) obj;
			return InvokerHelper.invokeMethod(function, "call", args);
		}
		else {
			throw new IllegalArgumentException(MessageFormat.format("{0}.{1} is not a function.",
					category, name));
		}
	}
}
