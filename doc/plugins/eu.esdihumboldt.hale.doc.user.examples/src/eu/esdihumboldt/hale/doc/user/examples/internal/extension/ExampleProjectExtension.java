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

package eu.esdihumboldt.hale.doc.user.examples.internal.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Extension point for example projects.
 * @author Simon Templer
 */
public class ExampleProjectExtension extends IdentifiableExtension<ExampleProject> {
	
	private static final ALogger log = ALoggerFactory.getLogger(ExampleProjectExtension.class);
	
	private static ExampleProjectExtension instance;
	
	/**
	 * Get the extension instance.
	 * @return the extension instance
	 */
	public static ExampleProjectExtension getInstance() {
		if (instance == null) {
			instance = new ExampleProjectExtension();
		}
		return instance;
	}

	/**
	 * Extension point ID
	 */
	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.doc.user.examples";

	/**
	 * Default constructor
	 */
	public ExampleProjectExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#create(java.lang.String, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected ExampleProject create(String elementId,
			IConfigurationElement element) {
		if ("example-project".equals(element.getName())) {
			try {
				return new ExampleProject(elementId, element);
			} catch (Exception e) {
				log.error("Error initializing example project", e);
				return null;
			}
		}
		
		return null;
	}
	
}