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

package eu.esdihumboldt.hale.io.csv.reader.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.springframework.core.convert.ConversionService;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.OsgiUtils;

/**
 * An extension for the property type
 * 
 * @author Kevin Mais
 */
public class PropertyTypeExtension extends IdentifiableExtension<PropertyType> {

	private static final ALogger log = ALoggerFactory
			.getLogger(PropertyTypeExtension.class);

	/**
	 * the property type identifier
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.csv.propertyType";

	private static PropertyTypeExtension instance;

	/**
	 * constructor
	 */
	public PropertyTypeExtension() {
		super(ID);
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#create(java.lang.String,
	 *      org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected PropertyType create(String id, IConfigurationElement conf) {
		ConversionService conversionService = OsgiUtils
				.getService(ConversionService.class);
		Class<?> binding = ExtensionUtil.loadClass(conf, "binding");
		if (conversionService.canConvert(String.class, binding)) {

			return new PropertyType(id, conf.getAttribute("name"), binding);
		}
		log.warn("Ignoring property type " + conf.getAttribute("name")
				+ " as conversion to " + binding.getSimpleName()
				+ " is not possible");
		return null;
	}

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

}
