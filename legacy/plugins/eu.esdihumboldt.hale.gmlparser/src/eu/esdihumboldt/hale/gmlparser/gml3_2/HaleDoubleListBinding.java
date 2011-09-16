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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml3.bindings.DoubleListBinding;
import org.geotools.gml3.v3_2.GML;
import org.geotools.xml.InstanceComponent;
import org.geotools.xml.impl.InstanceBinding;

/**
 * Improved double list binding
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleDoubleListBinding extends org.geotools.gml3.bindings.DoubleListBinding
	implements InstanceBinding {
    
    @Override
	public Object parse(InstanceComponent instance, Object value)
        throws Exception {
    	
    	if (value != null) {
    		return super.parse(instance, value);
    	}
    	else {
    		// parse double list from instance
    		String[] items = instance.getText().split("\\s"); //$NON-NLS-1$
    		List<Double> result = new ArrayList<Double>();
    		for (String item : items) {
    			if (!item.isEmpty()) {
					Double dvalue = Double.valueOf(item);
					if (dvalue != null) {
						result.add(dvalue);
					}
    			}
    		}
    		
    		return result.toArray(new Double[result.size()]);
    	}
    }

	/**
	 * @see DoubleListBinding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return GML.doubleList;
	}
    
}

