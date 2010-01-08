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
package eu.esdihumboldt.cst.transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;

import eu.esdihumboldt.cst.align.ext.IParameter;

/**
 * A default implementation of the {@link CstFunction} interface that implements 
 * all methods except {@link CstFunction#transform(FeatureCollection)} and 
 * {@link CstFunction}{@link #transform(Feature)}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractCstFunction 
	implements CstFunction {
	
	protected Map<String, String> parameters = null;	

	/**
	 * Map of parameter name and its type.
	 */
	protected final Map<String, Class<?>> parameterTypes = new HashMap<String, Class<?>>();
	/**
	 * @see CstFunction#configure(List)
	 */
	
	public AbstractCstFunction(){
		setParametersTypes(parameterTypes);
	}
	public final boolean configure(List<IParameter> parameters) {
		Map<String, String> params = new HashMap<String, String>();
		for (IParameter p : parameters){
			params.put(p.getName(), p.getValue());
		}
		configure(params);	
		return true;
	}
	
	/**
	 * @see CstFunction#configure(Map)
	 * 
	 * Simple configuration, just storing the map for later use.
	 * Note that subclasses may override this method if they want to implement 
	 * custom configuration logic.
	 */
	public boolean configure(Map<String, String> parametersValues) {
		this.parameters = parametersValues;
		return true;
	}
	
	protected abstract void setParametersTypes(Map<String, Class<?>> parametersTypes);
		
	public final Map<String, Class<?>> getParameterTypes() {
		Map<String, Class<?>> parameterTypeCopy = new HashMap<String, Class<?>>(parameterTypes);
		return parameterTypeCopy;
	}
	
}
