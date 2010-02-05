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

import java.util.List;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;

public class AlgorithmCST extends Model{
	
	public List <IParameter> parameters = null;	// parameters of algorithm
	public int numberOfParameters = 0;			// number of input parameters
	public boolean transformationOnEntity1 = false;
	public boolean transformationOnEntity2 = false;
	
	/**
	 * constructor
	 * @param title name of algorithm	
	 * @param functionID function ID of algorithm
	 * @param parameters input parameters of algorithm
	 */
	public AlgorithmCST(String title, String functionID, ICell cell) {
		super(title, functionID);
		try{
			this.parameters = cell.getEntity1().getTransformation().getParameters();
			transformationOnEntity1 = true;
		}
		catch(NullPointerException e){
			try{
				this.parameters = cell.getEntity2().getTransformation().getParameters();
				transformationOnEntity2 = true;
			}
			catch(NullPointerException ee){
				transformationOnEntity1 = true;
			}
		}
		
		if (this.parameters != null){
			numberOfParameters = parameters.size();
			//System.out.println(parameters.toString());
		}
		
	}
	
	/**
	 * Method that get parameters of algorithm
	 * @return Map <String, Class<?>> of type input parameters
	 */
	public List <IParameter> getParameters(){
		return this.parameters;
	}
}
