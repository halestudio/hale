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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.scripting;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionUtil;

/**
 * Factory for {@link Script}s.
 * 
 * @author Kai Schwierczek
 */
public class ScriptFactory extends AbstractConfigurationFactory<Script> {

	/**
	 * Create a {@link Script} factory based on the given configuration element.
	 * 
	 * @param conf the configuration element
	 */
	protected ScriptFactory(IConfigurationElement conf) {
		super(conf, "class");
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(Script script) {
		// nothing to do
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return conf.getAttribute("name");
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * Returns all allowed argument types.
	 * 
	 * @return all allowed argument types
	 */
	public Iterable<Class<?>> getArgumentBindings() {
		Collection<Class<?>> result = new ArrayList<Class<?>>();
		IConfigurationElement[] elements = conf.getChildren("argumentBinding");

		if (elements.length == 0)
			result.add(Object.class);
		else
			for (IConfigurationElement element : elements)
				result.add(ExtensionUtil.loadClass(element, "class"));

		return result;
	}

	/**
	 * Returns all possible return types.
	 * 
	 * @return all possible return types
	 */
	public Iterable<Class<?>> getResultBindings() {
		Collection<Class<?>> result = new ArrayList<Class<?>>();
		IConfigurationElement[] elements = conf.getChildren("resultBinding");

		if (elements.length == 0)
			result.add(Object.class);
		else
			for (IConfigurationElement element : elements)
				result.add(ExtensionUtil.loadClass(element, "class"));

		return result;
	}
}
