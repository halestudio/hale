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

package eu.esdihumboldt.hale.schemaprovider.model;

import org.opengis.feature.type.Name;

@Deprecated
public interface IDefaultGeometries {

	/**
	 * Get the default geometry name for a given type name
	 * 
	 * @param typeName
	 *            the type name
	 * 
	 * @return the default geometry property name or <code>null</code>
	 */
	public String getDefaultGeometryName(Name typeName);

	/**
	 * Set the default geometry property name for a given type
	 * 
	 * @param typeName
	 *            the type name
	 * @param propertyName
	 *            the geometry property name
	 */
	public void setDefaultGeometryName(Name typeName, String propertyName);
}
