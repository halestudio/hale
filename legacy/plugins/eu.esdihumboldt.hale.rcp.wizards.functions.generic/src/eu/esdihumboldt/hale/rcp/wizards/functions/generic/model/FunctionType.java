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
 * Classname    : eu.esdihumboldt.hale.rcp.wizards.functions.generic.model.FunctionType.java 
 * 
 * Author       : Josef Bezdek
 * 
 * Created on   : Jan, 2010
 *
 */

package eu.esdihumboldt.hale.rcp.wizards.functions.generic.model;

import java.util.ArrayList;
import java.util.List;

public class FunctionType extends Model {
	protected List <FunctionType> boxes;
	protected List <AlgorithmCST> inspire;
	protected List <AlgorithmCST> core;
	protected List <AlgorithmCST> others; 

	
	/**
	 * constructor for root
	 */
	public FunctionType() {
		boxes =  new ArrayList<FunctionType>();

		inspire = new ArrayList<AlgorithmCST>();
		core = new ArrayList<AlgorithmCST>();
		others = new ArrayList<AlgorithmCST>();
	}
	
	/**
	 * constructor for one item
	 * @param name
	 */
	public FunctionType(String name) {
		this();
		this.name = name;
	}
	
	/**
	 * adds new box
	 * @param box box name
	 */
	public void addBox(FunctionType box) {
		boxes.add(box);
		box.parent = this;
	}
	
	/**
	 * Method gets all boxes
	 * @return list of boxes
	 */
	public List <FunctionType> getBoxes() {
		return boxes;
	}
	
	/**
	 * Method gets all core functions
	 * @return list of core functions
	 */
	public List <AlgorithmCST> getCoreFunctions() {
		return core;
	}
	
	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addCoreFunction(AlgorithmCST item) {
		core.add(item);
		item.parent = this;
	}
	
	/**
	 * Method gets all others functions
	 * @return list of others functions
	 */
	public List <AlgorithmCST> getOthersFunctions() {
		return others;
	}
	
	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addOthersFunction(AlgorithmCST item) {
		others.add(item);
		item.parent = this;
	}
	

	/**
	 * Method gets all Inspire functions
	 * @return list of Inspire functions
	 */
	public List <AlgorithmCST> getInspireFunctions() {
		return inspire;
	}
	
	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addInspireFunction(AlgorithmCST item) {
		inspire.add(item);
		item.parent = this;
	}
	

	/** Answer the total number of items the
	 * receiver contains.
	 */
	public int size() {
		return getCoreFunctions().size() + getInspireFunctions().size();
	}

}
