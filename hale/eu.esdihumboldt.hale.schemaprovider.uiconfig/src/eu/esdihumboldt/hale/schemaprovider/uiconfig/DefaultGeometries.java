/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.schemaprovider.uiconfig;

import org.eclipse.ui.PlatformUI;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.models.ConfigSchemaService;
import eu.esdihumboldt.hale.schemaprovider.model.IDefaultGeometries;

/**
 * Manages default geometry preferences
 *
 * @author Simon Templer, Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefaultGeometries implements IDefaultGeometries {
	
	/**
	 * Section name for storing the default geometry name
	 */
	private static final String DEFAULT_GEOMETRY = "defaultGeometry";

	private static DefaultGeometries instance = new DefaultGeometries();
	
	/**
	 * Get the default instance
	 * 
	 * @return the default instance
	 */
	public static DefaultGeometries getInstance() {
		return instance;
	}
	
	/**
	 * Get the default geometry name for a given type name
	 * 
	 * @param typeName the type name
	 * 
	 * @return the default geometry property name or <code>null</code>
	 */
	public String getDefaultGeometryName(Name typeName) {
		ConfigSchemaService css = (ConfigSchemaService)PlatformUI.getWorkbench().getService(ConfigSchemaService.class);
		return css.getItem(DEFAULT_GEOMETRY, encodeNodeName(typeName.getNamespaceURI()));
	}
	
	private static String encodeNodeName(String name) {
		while (name.contains("//")) { //$NON-NLS-1$
			name = name.replaceAll("//", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return name;
	}
	
	/**
	 * Set the default geometry property name for a given type
	 * 
	 * @param typeName the type name
	 * @param propertyName the geometry property name
	 */
	public void setDefaultGeometryName(Name typeName, String propertyName) {
		ConfigSchemaService css = (ConfigSchemaService)PlatformUI.getWorkbench().getService(ConfigSchemaService.class);
		css.addItem(DEFAULT_GEOMETRY, encodeNodeName(typeName.getNamespaceURI()), propertyName);
	}

}
