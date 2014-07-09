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

package eu.esdihumboldt.cst.extension.hooks.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook.TreeState;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHookFactory;

/**
 * Default {@link TransformationTreeHook} factory based on a configuration
 * element.
 * 
 * @author Simon Templer
 */
public class DefaultTreeHookFactory extends AbstractConfigurationFactory<TransformationTreeHook>
		implements TransformationTreeHookFactory {

	private final TreeState state;

	/**
	 * Create a {@link TransformationTreeHook} factory from the given
	 * configuration element.
	 * 
	 * @param conf the configuration element
	 * @param state the associated tree state
	 */
	public DefaultTreeHookFactory(IConfigurationElement conf, TreeState state) {
		super(conf, "hook");

		this.state = state;
	}

	/**
	 * @see ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(TransformationTreeHook instance) {
		// TODO allow to free any resources
	}

	/**
	 * @see ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * @see ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return conf.getAttribute("name");
	}

	/**
	 * @see TransformationTreeHookFactory#getTreeState()
	 */
	@Override
	public TreeState getTreeState() {
		return state;
	}

}
