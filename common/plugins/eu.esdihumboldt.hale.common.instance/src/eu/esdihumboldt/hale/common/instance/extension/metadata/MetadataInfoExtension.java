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

package eu.esdihumboldt.hale.common.instance.extension.metadata;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Extension point for metadatas
 * 
 * @author Sebastian Reinhardt
 */
public class MetadataInfoExtension extends IdentifiableExtension<MetadataInfo> {

	private static final ALogger log = ALoggerFactory.getLogger(MetadataInfoExtension.class);

	private static MetadataInfoExtension instance;

	/**
	 * Extension point ID
	 */
	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.instance.metadata";

	/**
	 * Default constructor
	 */
	public MetadataInfoExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#create(java.lang.String,
	 *      org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected MetadataInfo create(String key, IConfigurationElement conf) {
		try {
			return new MetadataInfo(key, conf);
		} catch (Exception e) {
			log.error("Error initializing metadata", e);
			return null;
		}
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "key";
	}

	/**
	 * Get the extension instance.
	 * 
	 * @return the extension instance
	 */
	public static MetadataInfoExtension getInstance() {
		if (instance == null) {
			instance = new MetadataInfoExtension();
		}
		return instance;
	}

}
