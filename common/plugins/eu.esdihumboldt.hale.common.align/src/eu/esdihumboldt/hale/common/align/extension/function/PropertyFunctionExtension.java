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

package eu.esdihumboldt.hale.common.align.extension.function;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

/**
 * Extension for {@link PropertyFunction}s
 * 
 * @author Simon Templer
 */
public class PropertyFunctionExtension extends AbstractFunctionExtension<PropertyFunction> {

	/**
	 * Property function extension point
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.align.function";

	private static PropertyFunctionExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the extension
	 */
	public static PropertyFunctionExtension getInstance() {
		if (instance == null) {
			instance = new PropertyFunctionExtension();
		}

		return instance;
	}

	/**
	 * Default constructor
	 */
	protected PropertyFunctionExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "identifier";
	}

	/**
	 * @see AbstractFunctionExtension#doCreate(String, IConfigurationElement)
	 */
	@Override
	protected PropertyFunction doCreate(String elementId, IConfigurationElement element) {
		if (element.getName().equals("propertyFunction")) {
			return new PropertyFunction(element);
		}

		return null;
	}

}
