/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.service.impl;

import java.net.URL;
import java.util.Map;

import eu.esdihumboldt.cst.transformer.FunctionDescription;

public class FunctionDescriptionImpl implements FunctionDescription {

	private final URL url;
	private final Map<String, Class<?>> parameters;
	

	public FunctionDescriptionImpl(URL url, Map<String, Class<?>> parameters) {
		super();
		this.url = url;
		this.parameters = parameters;
	}
	
	public URL getFunctionId() {
		// TODO Auto-generated method stub
		return url;
	}


	public Map<String, Class<?>> getParameterConfiguration() {
		// TODO Auto-generated method stub
		return parameters;
	}

}
