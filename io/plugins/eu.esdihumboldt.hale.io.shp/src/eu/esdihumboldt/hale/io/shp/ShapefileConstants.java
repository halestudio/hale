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

package eu.esdihumboldt.hale.io.shp;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeInstanceReader;

/**
 * Constants for Shapefile I/O
 * 
 * @author Simon Templer
 */
public interface ShapefileConstants {

	/**
	 * The default shapefile namespace
	 */
	public static final String SHAPEFILE_NS = "http://www.esdi-humboldt.eu/hale/shp";

	/**
	 * The default type name
	 */
	public static final String DEFAULT_TYPE_NAME = "Shapefile";

	/**
	 * Name of the parameter for {@link ShapeInstanceReader} to select the type
	 * the instances should be associated to. The value is a {@link QName}
	 * encoded as String.
	 */
	public static final String PARAM_TYPENAME = "typename";

}
