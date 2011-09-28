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

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;

/**
 * TODO Type description
 * @author Baboo
 */
public class PropertyTypeExtension extends IdentifiableExtension<PropertyType>{

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
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#create(java.lang.String, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected PropertyType create(String id, IConfigurationElement conf) {
		return new PropertyType(id, conf.getAttribute("name"), ExtensionUtil.loadClass(conf, "binding"));
	}
	
	public static PropertyTypeExtension getInstance() {
		if(instance == null) {
			instance = new PropertyTypeExtension();
		}
		return instance;
		
	}

}
