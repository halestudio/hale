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
 * that can be included in a single Property instance. 
 * 
 *
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$ 
 */
public class PropertyComposition {
	
	/**
	 * Property Operator
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
	 * Constructor
	 * @param PropertyOperator operator
	 */
	public PropertyComposition(PropertyOperator operator){
		//property operator is a mandatory element in the corresponding omwg-schema
		this.operator = operator;
		this.collection = new ArrayList<Property>();
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
	 * @param collection the collection to set
	 */
	public void setCollection(List<Property> collection) {
		this.collection = collection;
	}

	/**
	 * @return the property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property the property to set
	 */
	public void setProperty(Property property) {
		this.property = property;
	}

	/**
	 * @return the relation
	 */
	public Relation getRelation() {
		return relation;
	}

	/**
	 * @param relation the relation to set
	 */
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
}
