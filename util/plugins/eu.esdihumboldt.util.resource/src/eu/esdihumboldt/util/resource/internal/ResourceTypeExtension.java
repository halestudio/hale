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

package eu.esdihumboldt.util.resource.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

/**
 * Resource type extension
 * 
 * @author Simon Templer
 */
public class ResourceTypeExtension extends IdentifiableExtension<ResourceType> {

	/**
	 * The resource extension point ID
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.util.resource";

	private static ResourceTypeExtension instance;

	/**
	 * Get the resource type extension.
	 * 
	 * @return the extension instance
	 */
	public static ResourceTypeExtension getInstance() {
		if (instance == null) {
			instance = new ResourceTypeExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	protected ResourceTypeExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see IdentifiableExtension#create(String, IConfigurationElement)
	 */
	@Override
	protected ResourceType create(String id, IConfigurationElement conf) {
		if (conf.getName().equals("resourceType")) {
			return new ResourceType(id, conf.getAttribute("name"));
		}
		return null;
	}

	/**
	 * @see IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

}
