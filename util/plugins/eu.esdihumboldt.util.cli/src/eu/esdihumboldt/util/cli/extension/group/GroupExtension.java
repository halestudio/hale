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

package eu.esdihumboldt.util.cli.extension.group;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.util.cli.extension.command.CommandExtension;

/**
 * CLI command group extension.
 * 
 * @author Simon Templer
 */
public class GroupExtension extends IdentifiableExtension<Group> {

	private static GroupExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the extension
	 */
	public static GroupExtension getInstance() {
		if (instance == null) {
			instance = new GroupExtension();
		}

		return instance;
	}

	/**
	 * Default constructor
	 */
	protected GroupExtension() {
		super(CommandExtension.EXTENSION_ID);
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	@Override
	protected Group create(String elementId, IConfigurationElement element) {
		if ("group".equals(element.getName())) {
			return new Group(elementId, element.getAttribute("name"),
					element.getAttribute("parent"), element.getAttribute("description"));
		}
		return null;
	}

}
