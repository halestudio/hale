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

import java.util.Map;

import org.geotools.xml.Configuration;
import org.geotools.xs.XS;
import org.geotools.xs.XSConfiguration;

/**
 * Extended {@link XSConfiguration} with some custom bindings 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleXSConfiguration extends XSConfiguration {

	/**
	 * @see Configuration#configureBindings(Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void configureBindings(Map bindings) {
		super.configureBindings(bindings);
		
		//XXX for whatever reason this is never executed
		
		bindings.put(XS.UNSIGNEDLONG, HaleXSUnsignedLongBinding.class);
		bindings.put(XS.UNSIGNEDINT, HaleXSUnsignedIntBinding.class);
		bindings.put(XS.UNSIGNEDSHORT, HaleXSUnsignedShortBinding.class);
		bindings.put(XS.UNSIGNEDBYTE, HaleXSUnsignedByteBinding.class);
	}

}
