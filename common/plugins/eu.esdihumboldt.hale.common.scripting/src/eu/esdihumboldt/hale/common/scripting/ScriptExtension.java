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

package eu.esdihumboldt.hale.common.scripting;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;

/**
 * Extension for {@link Script}s.
 * 
 * @author Kai Schwierczek
 */
public class ScriptExtension extends AbstractExtension<Script, ScriptFactory> {

	/**
	 * The extension point ID.
	 */
	public static final String ID = "eu.esdihumboldt.hale.script";

	private static ScriptExtension instance;

	/**
	 * Get the configuration page extension instance
	 * 
	 * @return the extension instance
	 */
	public static ScriptExtension getInstance() {
		if (instance == null) {
			instance = new ScriptExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	private ScriptExtension() {
		super(ID);
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.AbstractExtension#createFactory(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected ScriptFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ScriptFactory(conf);
	}

	/**
	 * Returns all script factories supporting the given return binding.
	 * 
	 * @param supportedReturnBinding the return binding that should be supported
	 * @return all script factories that match the given binding
	 */
	public Iterable<ScriptFactory> getScripts(Class<?> supportedReturnBinding) {
		Collection<ScriptFactory> scripts = new ArrayList<ScriptFactory>();
		for (ScriptFactory factory : getFactories()) {
			boolean returnBindingSupported = false;
			for (Class<?> returnBinding : factory.getResultBindings())
				if (returnBinding.isAssignableFrom(supportedReturnBinding))
					returnBindingSupported = true;

			if (returnBindingSupported)
				scripts.add(factory);
		}
		return scripts;
	}
}
