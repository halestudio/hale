/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.extension.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;

/**
 * Abstract function extension
 * @param <T> the function type 
 * 
 * @author Simon Templer
 */
public abstract class AbstractFunctionExtension<T extends AbstractFunction<?>> extends IdentifiableExtension<T> {

	private SetMultimap<String, T> categoryFunctions;
	
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
	 * @param category the category ID, may be <code>null</code>
	 * @return the list of functions or an empty list
	 */
	public List<T> getFunctions(String category) {
		if (categoryFunctions == null) {
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
	 * Create an object for the given configuration element
	 * @param elementId the element ID
	 * @param element the configuration element
	 * @return the element object or <code>null</code>
	 */
	protected abstract T doCreate(String elementId, IConfigurationElement element);

}
