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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.generic.model;

import java.util.ArrayList;
import java.util.List;

public class FunctionType extends Model {
	protected List <FunctionType> boxes;
	protected List <AlgorithmCST> clasification;
	protected List <AlgorithmCST> filter;
	protected List <AlgorithmCST> geometric;
	protected List <AlgorithmCST> inspire;
	protected List <AlgorithmCST> literal; 
	protected List <AlgorithmCST> math;
	protected List <AlgorithmCST> numeric;
	protected List <AlgorithmCST> other;
	
	/**
	 * constructor for root
	 */
	public FunctionType() {
		boxes =  new ArrayList<FunctionType>();
		clasification = new ArrayList<AlgorithmCST>();
		filter = new ArrayList<AlgorithmCST>();
		geometric = new ArrayList<AlgorithmCST>();
		inspire = new ArrayList<AlgorithmCST>();
		literal = new ArrayList<AlgorithmCST>();
		math = new ArrayList<AlgorithmCST>();
		numeric = new ArrayList<AlgorithmCST>();
		other = new ArrayList<AlgorithmCST>();
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
	 * Method gets all clasification functions
	 * @return list of clasification functions
	 */
	public List <AlgorithmCST> getClasificationFunctions() {
		return clasification;
	}
	
	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addClasificationFunction(AlgorithmCST item) {
		clasification.add(item);
		item.parent = this;
	}
	
	/**
	 * Method gets all filter functions
	 * @return list of filter functions
	 */
	public List <AlgorithmCST> getFilterFunctions() {
		return filter;
	}

	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addFilterFunction(AlgorithmCST item) {
		filter.add(item);
		item.parent = this;
	}
	
	/**
	 * Method gets all geometric functions
	 * @return list of geometric functions
	 */
	public List <AlgorithmCST> getGeometricFunctions() {
		return geometric;
	}
	
	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addGeometricFunction(AlgorithmCST item) {
		geometric.add(item);
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
	
	/**
	 * Method gets all literal functions
	 * @return list of literal functions
	 */
	public List <AlgorithmCST> getLiteralFunctions() {
		return literal;
	}

	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addLiteralFunction(AlgorithmCST item) {
		literal.add(item);
		item.parent = this;
	}

	/**
	 * Method gets all Math functions
	 * @return list of Math functions
	 */
	public List <AlgorithmCST> getMathFunctions() {
		return math;
	}

	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addMathFunction(AlgorithmCST item) {
		math.add(item);
		item.parent = this;
	}

	/**
	 * Method gets all numeric functions
	 * @return list of numeric functions
	 */
	public List <AlgorithmCST> getNumericFunctions() {
		return numeric;
	}
	
	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addNumericFunction(AlgorithmCST item) {
		numeric.add(item);
		item.parent = this;
	}

	/**
	 * Method gets all other functions
	 * @return list of other functions
	 */
	public List <AlgorithmCST> getOtherFunctions() {
		return other;
	}

	/**
	 * adds new function
	 * @param item name of function
	 */
	public void addOtherFunction(AlgorithmCST item) {
		other.add(item);
		item.parent = this;
	}

	/** Answer the total number of items the
	 * receiver contains.
	 */
	public int size() {
		return getClasificationFunctions().size() + getFilterFunctions().size() + getGeometricFunctions().size() +
		getInspireFunctions().size() + getLiteralFunctions().size() + getMathFunctions().size() + getNumericFunctions().size() +
		getOtherFunctions().size();
	}

}
