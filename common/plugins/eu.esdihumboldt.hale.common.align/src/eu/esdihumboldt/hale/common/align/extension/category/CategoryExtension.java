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

package eu.esdihumboldt.hale.common.align.extension.category;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;

/**
 * Function category extension
 * 
 * @author Simon Templer
 */
public class CategoryExtension extends IdentifiableExtension<Category> {

	/**
	 * The function category extension point ID
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.category";

	private static CategoryExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the extension
	 */
	public static CategoryExtension getInstance() {
		if (instance == null) {
			instance = new CategoryExtension();
		}

		return instance;
	}

	/**
	 * Default constructor
	 */
	protected CategoryExtension() {
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
	 * @see IdentifiableExtension#create(String, IConfigurationElement)
	 */
	@Override
	protected Category create(String elementId, IConfigurationElement element) {
		return new Category(elementId, element.getAttribute("name"),
				element.getAttribute("description"));
	}

}
