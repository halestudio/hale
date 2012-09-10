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
