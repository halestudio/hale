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

package eu.esdihumboldt.hale.io.gml.geometry;

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Base class for geometry handlers.
 * @author Simon Templer
 */
public abstract class AbstractGeometryHandler implements GeometryHandler,
		GeometryConstants {
	
	private Set<QName> supportedTypes;
	
	private static final GeometryFactory factory = new GeometryFactory();

	/**
	 * @see GeometryHandler#getSupportedTypes()
	 */
	@Override
	public Set<QName> getSupportedTypes() {
		if (supportedTypes == null) {
			supportedTypes = Collections.unmodifiableSet(initSupportedTypes());
		}
		return supportedTypes;
	}

	/**
	 * Create the set of supported types.
	 * @return the set of supported type names
	 */
	protected abstract Set<? extends QName> initSupportedTypes();
	
	/**
	 * Get a geometry factory instance.
	 * @return the geometry factory
	 */
	protected GeometryFactory getGeometryFactory() {
		//XXX instead retrieve from a service?
		return factory;
	}

}
