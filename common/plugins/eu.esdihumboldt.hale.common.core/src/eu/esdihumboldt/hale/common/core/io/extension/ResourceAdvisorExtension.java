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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.content.IContentType;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.ResourceAdvisor;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultResourceAdvisor;

/**
 * Extension for {@link ResourceAdvisor}s
 * 
 * @author Simon Templer
 */
public class ResourceAdvisorExtension extends
		AbstractExtension<ResourceAdvisor, ResourceAdvisorDescriptor> {

	/**
	 * {@link ResourceAdvisor} factory based on a {@link IConfigurationElement}
	 */
	private static class ConfigurationFactory extends AbstractConfigurationFactory<ResourceAdvisor>
			implements ResourceAdvisorDescriptor {

		/**
		 * Create the {@link ResourceAdvisor} factory
		 * 
		 * @param conf the configuration element
		 */
		protected ConfigurationFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see ExtensionObjectFactory#dispose(Object)
		 */
		@Override
		public void dispose(ResourceAdvisor advisor) {
			// do nothing
		}

		/**
		 * @see ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return getIdentifier();
		}

		/**
		 * @see ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

		@Override
		public Set<IContentType> getAssociatedTypes() {
			IConfigurationElement[] children = conf.getChildren("contentType");

			if (children != null) {
				Set<IContentType> result = new HashSet<IContentType>();

				for (IConfigurationElement child : children) {
					String id = child.getAttribute("ref");
					IContentType ct = HalePlatform.getContentTypeManager().getContentType(id);
					if (ct != null) {
						result.add(ct);
					}
					else {
						log.error(MessageFormat.format(
								"Content type with ID {0} not known by the platform", id));
					}
				}

				return result;
			}
			else {
				return Collections.emptySet();
			}
		}

	}

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.resource";

	private static final ALogger log = ALoggerFactory.getLogger(ResourceAdvisorExtension.class);

	/**
	 * Default resource advisor
	 */
	private static final ResourceAdvisor DEFAULT = new DefaultResourceAdvisor();

	private static ResourceAdvisorExtension instance;

	/**
	 * Get the I/O provider extension instance
	 * 
	 * @return the extension instance
	 */
	public static ResourceAdvisorExtension getInstance() {
		if (instance == null) {
			instance = new ResourceAdvisorExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private ResourceAdvisorExtension() {
		super(ID);
	}

	@Override
	protected ResourceAdvisorDescriptor createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("advisor")) {
			return new ConfigurationFactory(conf);
		}
		return null;
	}

	/**
	 * Create an I/O advisor for the given content type.
	 * 
	 * @param contentType the content type, may be <code>null</code>
	 * @return the resource advisor for that content type or a default resource
	 *         advisor
	 */
	public ResourceAdvisor getAdvisor(IContentType contentType) {
		if (contentType == null) {
			return DEFAULT;
		}

		boolean error = false;
		for (ResourceAdvisorDescriptor factory : getFactories()) {
			if (factory.getAssociatedTypes().contains(contentType)) {
				try {
					return factory.createExtensionObject();
				} catch (Exception e) {
					log.error("Could not create resource advisor " + factory.getIdentifier(), e);
					error = true;
				}
			}
		}

		if (error) {
			log.warn("Using default resource advisor as fall-back for content type "
					+ contentType.getName());
		}

		return DEFAULT;
	}

}
