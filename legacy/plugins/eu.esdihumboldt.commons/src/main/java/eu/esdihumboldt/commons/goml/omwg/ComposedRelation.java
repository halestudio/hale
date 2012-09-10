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
package eu.esdihumboldt.commons.goml.omwg;

import java.util.List;

import eu.esdihumboldt.specification.cst.rdf.IAbout;

/**
 * This class represents omwg:relConst. Used for building an expression that
 * constructs a Relation based on other Relations.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class ComposedRelation extends Relation {

	/**
	 * Indicates the (first) operator/predicate for this
	 * {@link ComposedRelation}.
	 */
	private RelationOperatorType operator;

	/**
	 * In case of AND and OR the operand is a collection of two or more
	 * (Composed)FeatureClasses. In case of a unary operator the operand is
	 * either: - a collection consisting of one (Composed)FeatureClass; - a
	 * (Composed)Relation. Only one of the two is allowed - at the moment not
	 * forced, must be dealt with by application code.
	 * 
	 */
	private List<FeatureClass> collection;

	// constructors ............................................................

	public ComposedRelation(IAbout about) {
		super(about);
	}

	// getters / setters .......................................................

	/**
	 * @return the collection
	 */
	public List<FeatureClass> getCollection() {
		return collection;
	}

	/**
	 * @param collection
	 *            the collection to set
	 */
	public void setCollection(List<FeatureClass> collection) {
		this.collection = collection;
	}

	/**
	 * @return the operator
	 */
	public RelationOperatorType getRelationOperatorType() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setRelationOperatorType(RelationOperatorType operator) {
		this.operator = operator;
	}

	public enum RelationOperatorType {
		AND, // intersection
		OR, // union
		NOT, INVERSE, SYMMETRIC, TRANSITIVE, REFLEXIVE, FIRST, NEXT
	}

}
