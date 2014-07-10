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

package eu.esdihumboldt.hale.ui.scripting;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;

/**
 * Factory for {@link ScriptUI}s.
 * 
 * @author Kai Schwierczek
 */
public class ScriptUIFactory extends AbstractConfigurationFactory<ScriptUI> {

	/**
	 * Create a {@link ScriptUI} factory based on the given configuration
	 * element.
	 * 
	 * @param conf the configuration element
	 */
	protected ScriptUIFactory(IConfigurationElement conf) {
		super(conf, "class");
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(ScriptUI script) {
		// nothing to do
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * Returns the script id the ScriptUI, which this factory is for, supports.
	 * 
	 * @return the script id the ScriptUI, which this factory is for, supports
	 */
	public String getScriptId() {
		return conf.getAttribute("script");
	}
}
