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

package eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Registry for {@link GeometryConverter}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GeometryConverterRegistry {
	
	private static final GeometryConverterRegistry INSTANCE = new GeometryConverterRegistry();
	
	/**
	 * Get the singleton instance of the registry
	 * 
	 * @return the geometry converter registry
	 */
	public static GeometryConverterRegistry getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Converters organized by source geometry type
	 */
	private final Map<Class<? extends Geometry>, Set<GeometryConverter<?, ?>>> converters = new HashMap<Class<? extends Geometry>, Set<GeometryConverter<?,?>>>();

	/**
	 * Default constructor 
	 */
	private GeometryConverterRegistry() {
		super();
		
		init();
	}
	
	/**
	 * Initialize the registry
	 */
	private void init() {
		// built-in converters
		
		//TODO other converters?
	}

	/**
	 * Register a geometry converter
	 * 
	 * @param converter the converter
	 */
	public void registerConverter(GeometryConverter<?, ?> converter) {
		Set<GeometryConverter<?, ?>> cs = converters.get(converter.getSourceType());
		if (cs == null) {
			cs = new HashSet<GeometryConverter<?,?>>();
			converters.put(converter.getSourceType(), cs);
		}
		
		cs.add(converter);
	}
	
}
