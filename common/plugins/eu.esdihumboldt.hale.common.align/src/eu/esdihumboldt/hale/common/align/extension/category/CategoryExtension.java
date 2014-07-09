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

package eu.esdihumboldt.hale.common.align.extension.category;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

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
