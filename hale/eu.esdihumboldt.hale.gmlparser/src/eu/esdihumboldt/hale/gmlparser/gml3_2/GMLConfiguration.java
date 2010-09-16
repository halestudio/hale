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

package eu.esdihumboldt.hale.gmlparser.gml3_2;

import java.util.Map;

import org.geotools.gml3.v3_2.GML;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GMLConfiguration extends org.geotools.gml3.v3_2.GMLConfiguration {

	/**
	 * @see org.geotools.xml.Configuration#configureBindings(java.util.Map)
	 */
	@Override
	protected void configureBindings(Map bindings) {
		super.configureBindings(bindings);
		
		HaleDoubleListBinding doubleListBinding = new HaleDoubleListBinding();
		bindings.put(GML.doubleList, doubleListBinding);
//		bindings.put(doubleListBinding.getTarget(), doubleListBinding);
	}

}
