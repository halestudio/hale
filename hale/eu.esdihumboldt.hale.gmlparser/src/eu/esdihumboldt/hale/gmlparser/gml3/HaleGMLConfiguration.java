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

package eu.esdihumboldt.hale.gmlparser.gml3;

import java.util.Map;

import org.geotools.gml3.GML;
import org.geotools.gml3.bindings.SurfaceArrayPropertyTypeBinding;
import org.geotools.xml.Configuration;

import eu.esdihumboldt.hale.gmlparser.binding.HaleReferenceTypeBinding;
import eu.esdihumboldt.hale.gmlparser.gml3_2.HaleMultiPolygonTypeBinding;
import eu.esdihumboldt.hale.gmlparser.gml3_2.HaleSurfaceTypeBinding;

/**
 * Extended GML 3 configuration
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleGMLConfiguration extends org.geotools.gml3.GMLConfiguration {

	/**
	 * @see Configuration#configureBindings(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void configureBindings(Map bindings) {
		super.configureBindings(bindings);
		
		bindings.put(GML.SurfacePatchArrayPropertyType, SurfaceArrayPropertyTypeBinding.class);
		
		bindings.put(GML.SurfaceType, HaleSurfaceTypeBinding.class);
		
		bindings.put(GML.MultiPolygonType, HaleMultiPolygonTypeBinding.class);
		
		bindings.put(GML.ReferenceType, HaleReferenceTypeBinding.class);
		
		bindings.put(GML.SurfacePropertyType, HaleSurfacePropertyTypeBinding.class);
		
//		bindings.put(new QName("http://www.opengis.net/gml","CurvePropertyType"), HaleCurvePropertyTypeBinding.class);
//		
//		bindings.put(new QName("http://www.opengis.net/gml","AbstractRingPropertyType"), HaleAbstractRingPropertyTypeBinding.class);
	}

}
