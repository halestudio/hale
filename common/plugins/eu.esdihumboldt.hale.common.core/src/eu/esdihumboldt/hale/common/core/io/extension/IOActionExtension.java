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

package eu.esdihumboldt.hale.common.core.io.extension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.common.core.io.IOAction;
import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Extension for {@link IOAction} definitions
 * 
 * @author Simon Templer
 */
public class IOActionExtension extends IdentifiableExtension<IOAction> {

	/**
	 * {@link IOAction} based on an {@link IConfigurationElement}
	 */
	private static class ConfigurationIOAction implements IOAction {

		private final IConfigurationElement conf;
		private final String id;

		/**
		 * Create the I/O action
		 * 
		 * @param id the action ID
		 * @param conf the configuration element defining the action
		 */
		public ConfigurationIOAction(String id, IConfigurationElement conf) {
			this.id = id;
			this.conf = conf;
		}

		/**
		 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable#getId()
		 */
		@Override
		public String getId() {
			return id;
		}

		/**
		 * @see IOAction#getProviderType()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends IOProvider> getProviderType() {
			return (Class<? extends IOProvider>) ExtensionUtil.loadClass(conf, "type");
		}

		/**
		 * @see IOAction#getDependencies()
		 */
		@Override
		public Set<String> getDependencies() {
			IConfigurationElement[] children = conf.getChildren("dependsOn");

			if (children != null) {
				Set<String> result = new HashSet<String>();

				for (IConfigurationElement child : children) {
					result.add(child.getAttribute("action"));
				}

				return result;
			}
			else {
				return Collections.emptySet();
			}
		}

		/**
		 * @see IOAction#getName()
		 */
		@Override
		public String getName() {
			return conf.getAttribute("name");
		}

		@Override
		public String getResourceName() {
			return conf.getAttribute("resourceName");
		}

		@Override
		public String getResourceCategoryName() {
			return conf.getAttribute("categoryName");
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConfigurationIOAction other = (ConfigurationIOAction) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			}
			else if (!id.equals(other.id))
				return false;
			return true;
		}

	}

	/**
	 * The action extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.action";

	private static IOActionExtension instance;

	/**
	 * Get the I/O action extension instance
	 * 
	 * @return the extension instance
	 */
	public static IOActionExtension getInstance() {
		if (instance == null) {
			instance = new IOActionExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private IOActionExtension() {
		super(ID);
	}

	/**
	 * @see IdentifiableExtension#create(String, IConfigurationElement)
	 */
	@Override
	protected IOAction create(String id, IConfigurationElement conf) {
		if (conf.getName().equals("action")) {
			return new ConfigurationIOAction(id, conf);
		}
		else {
			return null;
		}
	}

	/**
	 * @see IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

}
