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

package eu.esdihumboldt.hale.common.instance.extension.metadata;

import org.eclipse.core.runtime.IConfigurationElement;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;


/**
 * Extension point for metadatas
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
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#create(java.lang.String, org.eclipse.core.runtime.IConfigurationElement)
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
	 * @return the extension instance
	 */
	public static MetadataInfoExtension getInstance() {
		if (instance == null) {
			instance = new MetadataInfoExtension();
		}
		return instance;
	}

}
