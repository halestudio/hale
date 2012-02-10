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

package eu.esdihumboldt.util.resource.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;

/**
 * Resource type extension
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
