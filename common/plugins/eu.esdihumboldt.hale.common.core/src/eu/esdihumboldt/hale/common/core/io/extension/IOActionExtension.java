/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
