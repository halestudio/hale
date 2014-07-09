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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.scripting.ScriptExtension;

/**
 * Extension for {@link ScriptUI}s.
 * 
 * @author Kai Schwierczek
 */
public class ScriptUIExtension extends AbstractExtension<ScriptUI, ScriptUIFactory> {

	private static final ALogger log = ALoggerFactory.getLogger(ScriptUIExtension.class);

	/**
	 * The extension point ID.
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.script";

	private static ScriptUIExtension instance;

	/**
	 * Get the configuration page extension instance
	 * 
	 * @return the extension instance
	 */
	public static ScriptUIExtension getInstance() {
		if (instance == null) {
			instance = new ScriptUIExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	private ScriptUIExtension() {
		super(ID);
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.AbstractExtension#createFactory(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected ScriptUIFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ScriptUIFactory(conf);
	}

	/**
	 * Returns the first found ScriptUI for the given script id or
	 * <code>null</code> if there is none.
	 * 
	 * @param scriptId the script id
	 * @return a ScriptUI for the given script id or <code>null</code>
	 */
	public ScriptUI getScriptUI(final String scriptId) {
		// XXX return all available ScriptUIs for the given id?

		List<ScriptUIFactory> factories = getFactories(new FactoryFilter<ScriptUI, ScriptUIFactory>() {

			@Override
			public boolean acceptFactory(ScriptUIFactory factory) {
				return factory.getScriptId().equals(scriptId);
			}

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<ScriptUI, ScriptUIFactory> arg0) {
				return true;
			}
		});

		if (factories.isEmpty())
			return null;
		else
			try {
				ScriptUI scriptUI = factories.get(0).createExtensionObject();
				scriptUI.setScript(ScriptExtension.getInstance().getFactory(scriptId)
						.createExtensionObject());
				return scriptUI;
			} catch (Exception e) {
				log.warn("Exception creating ScriptUI", e);
				return null;
			}
	}
}
