/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.view.server;

import org.apache.commons.beanutils.BeanUtils;
import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.MapServerFactory;

/**
 * {@link MapServer} factory based on an {@link IConfigurationElement}
 * 
 * @author Simon Templer
 */
public class ConfigurationMapServerFactory extends AbstractConfigurationFactory<MapServer>
		implements MapServerFactory {

	/**
	 * @param config the configuration element
	 */
	ConfigurationMapServerFactory(IConfigurationElement config) {
		super(config, "class"); //$NON-NLS-1$
	}

	/**
	 * @see AbstractConfigurationFactory#createExtensionObject()
	 */
	@Override
	public MapServer createExtensionObject() throws Exception {
		MapServer server = super.createExtensionObject();

		// set name
		server.setName(getDisplayName());

		// configure map server
		IConfigurationElement[] properties = conf.getChildren();
		for (IConfigurationElement property : properties) {
			String name = property.getAttribute("name"); //$NON-NLS-1$
			String value = property.getAttribute("value"); //$NON-NLS-1$
			BeanUtils.setProperty(server, name, value);
		}

		return server;
	}

	/**
	 * @see ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return conf.getAttribute("name"); //$NON-NLS-1$
	}

	/**
	 * @see ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getTypeName() + ":" + getDisplayName(); //$NON-NLS-1$
	}

	/**
	 * @see ExtensionObjectFactory#dispose(Object)
	 */
	@Override
	public void dispose(MapServer instance) {
		instance.cleanup();
	}

	/**
	 * @see AbstractObjectDefinition#getPriority()
	 */
	@Override
	public int getPriority() {
		try {
			return Integer.valueOf(conf.getAttribute("priority")); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			return 0; // default
		}
	}

}
