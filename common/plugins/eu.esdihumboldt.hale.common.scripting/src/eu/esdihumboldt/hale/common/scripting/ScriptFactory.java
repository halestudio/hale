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

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;

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
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(Script script) {
		// nothing to do
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

}
