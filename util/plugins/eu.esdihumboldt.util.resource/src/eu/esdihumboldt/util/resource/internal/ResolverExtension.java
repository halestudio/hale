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
 * Resource resolver extension.
 * 
 * @author Simon Templer
 */
public class ResolverExtension extends IdentifiableExtension<ResolverConfiguration> {

	private static ResolverExtension instance;

	/**
	 * Get the bundle host resolver extension.
	 * 
	 * @return the extension instance
	 */
	public static ResolverExtension getInstance() {
		if (instance == null) {
			instance = new ResolverExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	protected ResolverExtension() {
		super(ResourceTypeExtension.EXTENSION_ID);
	}

	/**
	 * @see IdentifiableExtension#create(String, IConfigurationElement)
	 */
	@Override
	protected ResolverConfiguration create(String id, IConfigurationElement conf) {
		if (conf.getName().equals("resolver")) {
			return new ResolverConfiguration(id, conf);
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
