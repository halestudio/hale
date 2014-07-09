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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

/**
 * Abstract function extension
 * 
 * @param <T> the function type
 * 
 * @author Simon Templer
 */
public abstract class AbstractFunctionExtension<T extends AbstractFunction<?>> extends
		IdentifiableExtension<T> {

	private SetMultimap<String, T> categoryFunctions;

	private boolean initialized = false;

	/**
	 * @see IdentifiableExtension#IdentifiableExtension(String)
	 */
	public AbstractFunctionExtension(String extensionId) {
		super(extensionId);
	}

	/**
	 * Calls {@link #doCreate(String, IConfigurationElement)} to create an
	 * object for the given configuration element.
	 * 
	 * @see IdentifiableExtension#create(String, IConfigurationElement)
	 */
	@Override
	protected T create(String elementId, IConfigurationElement element) {
		T function = doCreate(elementId, element);
		if (function != null) {
			if (categoryFunctions == null) {
				categoryFunctions = LinkedHashMultimap.create();
			}
			categoryFunctions.put(function.getCategoryId(), function);
		}
		return function;
	}

	/**
	 * Get the functions associated to the category with the given ID
	 * 
	 * @param category the category ID, may be <code>null</code>
	 * @return the list of functions or an empty list
	 */
	public List<T> getFunctions(String category) {
		if (!initialized || categoryFunctions == null) {
			// initialize
			getElements();
		}

		if (categoryFunctions != null) {
			Set<T> res = categoryFunctions.get(category);
			return new ArrayList<T>(res);
		}

		return Collections.emptyList();
	}

	/**
	 * @see IdentifiableExtension#getElements()
	 */
	@Override
	public Collection<T> getElements() {
		try {
			return super.getElements();
		} finally {
			initialized = true;
		}
	}

	/**
	 * Create an object for the given configuration element
	 * 
	 * @param elementId the element ID
	 * @param element the configuration element
	 * @return the element object or <code>null</code>
	 */
	protected abstract T doCreate(String elementId, IConfigurationElement element);

}
