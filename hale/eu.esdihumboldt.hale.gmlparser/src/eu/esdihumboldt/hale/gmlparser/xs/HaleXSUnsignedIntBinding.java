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

package eu.esdihumboldt.hale.gmlparser.xs;

import org.geotools.xml.InstanceComponent;
import org.geotools.xs.bindings.XSUnsignedIntBinding;
import org.geotools.xs.bindings.XSUnsignedLongBinding;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleXSUnsignedIntBinding extends XSUnsignedIntBinding {

	/**
	 * @see XSUnsignedLongBinding#parse(InstanceComponent, Object)
	 */
	@Override
	public Object parse(InstanceComponent instance, Object value)
			throws Exception {
		// super class only accepts strings as value, but in some cases it seems
		// to be of another type, e.g. Integer
		return super.parse(instance, value.toString());
	}

}
