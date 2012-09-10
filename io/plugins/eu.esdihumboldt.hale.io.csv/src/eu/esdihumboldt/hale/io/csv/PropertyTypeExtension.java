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

package eu.esdihumboldt.hale.io.csv;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractExtension;

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
		 * @see de.cs3d.util.eclipse.extension.ExtensionObjectFactory#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(PropertyType arg0) {
			// ignore
		}

		/**
		 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return conf.getAttribute("name");
		}

		/**
		 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getIdentifier()
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
	 * @see de.cs3d.util.eclipse.extension.AbstractExtension#createFactory(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected PropertyTypeFactory createFactory(IConfigurationElement conf) throws Exception {
		return new DefaultFactory(conf);
	}

}
