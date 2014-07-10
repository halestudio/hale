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

package eu.esdihumboldt.hale.io.csv;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractExtension;

/**
 * Class for the property type extension
 * 
 * @author Kevin Mais
 */
public class PropertyTypeExtension extends AbstractExtension<PropertyType, PropertyTypeFactory> {

	/**
	 * Default implementation class
	 * 
	 * @author Kevin Mais
	 */
	private static class DefaultFactory extends AbstractConfigurationFactory<PropertyType>
			implements PropertyTypeFactory {

		/**
		 * Default constructor
		 * 
		 * @param conf the configuration element
		 */
		protected DefaultFactory(IConfigurationElement conf) {
			super(conf, "class");
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(PropertyType arg0) {
			// ignore
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

		/**
		 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getIdentifier()
		 */
		@Override
		public String getIdentifier() {
			return conf.getAttribute("id");
		}

	}

	private static volatile PropertyTypeExtension instance;

	/**
	 * Getter for the PropertyTypeExtension
	 * 
	 * @return an Object of PropertyTypeExtension
	 */
	public static PropertyTypeExtension getInstance() {
		if (instance == null) {
			instance = new PropertyTypeExtension();
		}
		return instance;

	}

	/**
	 * the property type identifier
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.csv.propertyType";

	/**
	 * Default constructor
	 */
	public PropertyTypeExtension() {
		super(ID);
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.AbstractExtension#createFactory(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected PropertyTypeFactory createFactory(IConfigurationElement conf) throws Exception {
		return new DefaultFactory(conf);
	}

}
