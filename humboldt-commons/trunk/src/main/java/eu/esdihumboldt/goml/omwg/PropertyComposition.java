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

package eu.esdihumboldt.goml.omwg;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the PropertyComposition
 *
 * <p>
 * The Single PropertyComposition Instance can contain:
 * <ul>
 * <li>collection of properties</li>
 * <li>single Property</li>
 * <li>single Relation</li>
 * </ul>
 * but nether the composition of these.
 * </p>
 * <p>
 *
 * 
 * 
 * 
 * 
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class PropertyComposition {
	
	/**
	 * Property Operator is a mandatory parameter for the Property Composition
	 */
	private PropertyOperator operator;
	
	/** collection of the properties */
	 private List<Property> collection; 

	/**
	 * Property
	 */
	private Property property; 
	
	/**
	 * Relation
	 */
	private Relation relation;
	
	/**
	 *	<p> 
	 * 	Constructor to put collection of Properties to the Property Composition
	 *  </p>
	 * 
	 * @param PropertyOperator operator
	 * @param List<Property> collection of the Properties
	 */
	public PropertyComposition(PropertyOperator operator, List<Property> collection){
		//property operator is a mandatory element in the corresponding omwg-schema
		this.operator = operator;
		this.collection = collection;
	}
	
	/**
	 *	<p> 
	 * 	Constructor to put a single Property to the Property Composition
	 *  </p>
	 * 
	 * @param PropertyOperator operator
	 * @param Property property
	 */
	public PropertyComposition(PropertyOperator operator, Property property){
		//property operator is a mandatory element in the corresponding omwg-schema
		this.operator = operator;
		this.property = property;
	}
	
	/**
	 *	<p> 
	 * 	Constructor to put Relation to the Property Composition
	 *  </p>r
	 * 
	 * @param PropertyOperator operator
	 * @param Relation relation
	 */
	public PropertyComposition(PropertyOperator operator, Relation relation){
		//property operator is a mandatory element in the corresponding omwg-schema
		this.operator = operator;
		this.relation = relation;
	}
	
	

	/**
	 * @return the operator
	 */
	public PropertyOperator getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(PropertyOperator operator) {
		this.operator = operator;
	}

	/**
	 * @return the collection
	 */
	public List<Property> getCollection() {
		return collection;
	}

	

	/**
	 * @return the property
	 */
	public Property getProperty() {
		return property;
	}

	
	/**
	 * @return the relation
	 */
	public Relation getRelation() {
		return relation;
	}

	
}
