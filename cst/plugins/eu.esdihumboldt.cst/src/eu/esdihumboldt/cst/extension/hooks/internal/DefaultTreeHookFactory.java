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

package eu.esdihumboldt.cst.extension.hooks.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
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
