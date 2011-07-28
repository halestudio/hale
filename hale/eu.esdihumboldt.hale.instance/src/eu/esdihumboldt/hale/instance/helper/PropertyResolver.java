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

package eu.esdihumboldt.hale.instance.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.instance.model.Instance;

/**
 * TODO Type description
 * @author Sebastian Reinhardt
 */
public class PropertyResolver {
	
	/**
	 * @param instance
	 * @param propertyPath
	 * @return
	 */
	public static Collection<Object> getValues(Instance instance, 
			String propertyPath) {
		
		
	 
		
		
		
		//the old way
		Object[] values = instance.getProperty(new QName(propertyPath));
		
		
		return null;
	}
	
	/**
	 * @param instance
	 * @param propertyPath
	 * @return
	 */
	public static boolean hasProperty(Instance instance, 
			String propertyPath) {
		//TODO
		
		
		
		ArrayList<String> pathParts = new ArrayList<String>();
		 StringTokenizer st = new StringTokenizer(propertyPath, ".");
			while(st.hasMoreTokens()){
				pathParts.add(st.nextToken());
			}
			
			
			
			
		
		//the old way
		return instance.getProperty(new QName(propertyPath)) != null;
		
		
	}

}
