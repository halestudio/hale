/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : hale
 * 	 
 * Classname    : eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.AlgorithmCST.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.generic.model;

import java.net.URL;
import java.util.Map;

public class AlgorithmCST extends Model{
	
	public Map <String, Class<?>> parameters;	// parameters of algorithm
	public int numberOfParameters = 0;			// number of input parameters
	
	/**
	 * constructor
	 * @param title name of algorithm	
	 * @param functionID function ID of algorithm
	 * @param parameters input parameters of algorithm
	 */
	public AlgorithmCST(String title, URL functionID, Map <String, Class<?>> parameters) {
		super(title, functionID);
		this.parameters = parameters;
		if (parameters != null){
			numberOfParameters = parameters.size();
		}
	}
	
	/**
	 * Method that get parameters of algorithm
	 * @return Map <String, Class<?>> of type input parameters
	 */
	public Map <String, Class<?>> getParameters(){
		return this.parameters;
	}
}
