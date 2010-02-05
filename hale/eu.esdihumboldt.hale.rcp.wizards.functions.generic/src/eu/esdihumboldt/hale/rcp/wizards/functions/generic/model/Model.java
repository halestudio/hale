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
 * Classname    : eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.Model.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.generic.model;

import java.net.URL;

public abstract class Model {
	protected FunctionType parent;
	protected String name;
	protected String functionID;
	
	/**
	 * constructor	
	 * @param title name of function
	 * @param functionID ID of algorithm
	 */
	public Model(String title, String functionID) {
		this.name = title;
		this.functionID = functionID;
	}
	
	/**
	 * sets name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * sets functionID
	 * @param functionID
	 */
	public void setFunctionID(String functionID){
		this.functionID = functionID;
	}
	
	/**
	 * Method gets parent(in tree) of this algorithm 
	 * @return parent
	 */
	public FunctionType getParent() {
		return parent;
	}
		
	/**
	 * Method gets name of algorithm
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Method gets function ID
	 * @return url
	 */
	public String getFunctionID(){
		return functionID;
	}
	
	/**
	 * implicit constructor
	 */
	public Model() {
	}	
	
	/**
	 * gets title
	 * @return title
	 */
	public String getTitle() {
		return name;
	}


}
