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
package eu.esdihumboldt.goml.omwg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.esdihumboldt.cst.rdf.IAbout;
import eu.esdihumboldt.goml.rdf.About;

/**
 * This class represents omwg:propertyConst. Used for building an expression that
 * constructs a Property based on other Properties.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class ComposedProperty 
	extends Property {

	/**
	 * Indicates the (first) operator/predicate for this {@link ComposedProperty}.
	 */
	private PropertyOperatorType operator;

	/**
	 * In case of AND and OR the operand is a collection of two or more (Composed)FeatureClasses.
       * In case of a unary operator the operand is either: 
       * - a collection consisting of one Property (or ComposedProperty);
       * - a Relation (or ComposedRelation).
       * Only one of the two is allowed - at the moment not forced, must be dealt with by application code.
	 * 
	 */
	private List<Property> collection;
	private Relation relation;

	
	// constructors ............................................................
	
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

	public ComposedProperty(IAbout about) {
		super(about);
		this.collection = new ArrayList<Property>();
	}
	
	//FIXME
	public ComposedProperty(String namespace) {
		this(new About(namespace, UUID.randomUUID().toString()));
	}

	// getters / setters .......................................................
	
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
	 * @return the operator
	 */
	public PropertyOperatorType getPropertyOperatorType() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setPropertyOperatorType(PropertyOperatorType operator) {
		this.operator = operator;
	}

	public enum PropertyOperatorType {
		AND, // intersection
		OR,  // union
		NOT,
		FIRST,
		NEXT
	}

}
