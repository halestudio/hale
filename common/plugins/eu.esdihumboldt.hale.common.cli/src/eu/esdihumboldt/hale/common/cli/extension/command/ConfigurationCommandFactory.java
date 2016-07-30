/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.cli.extension.command;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import eu.esdihumboldt.hale.common.cli.Command;

/**
 * Command factory based on a configuration element.
 * 
 * @author Simon Templer
 */
public class ConfigurationCommandFactory extends AbstractConfigurationFactory<Command>
		implements CommandFactory {

	/**
	 * Create a command factory based on a configuration element.
	 * 
	 * @param conf the configuration element
	 */
	public ConfigurationCommandFactory(IConfigurationElement conf) {
		super(conf, "class");
	}

	@Override
	public void dispose(Command instance) {
		// nothing to do
	}

	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	@Override
	public String getDisplayName() {
		return conf.getAttribute("name");
	}

	@Override
	public String getGroup() {
		return conf.getAttribute("group");
	}

}
