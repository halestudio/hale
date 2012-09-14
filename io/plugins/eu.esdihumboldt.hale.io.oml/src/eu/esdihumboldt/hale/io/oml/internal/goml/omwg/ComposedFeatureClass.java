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
package eu.esdihumboldt.hale.io.oml.internal.goml.omwg;

import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This class represents omwg:classConst. Used for building an expression that
 * constructs a FeatureClass based on other FeatureClasses.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public class ComposedFeatureClass extends FeatureClass {

	/**
	 * Indicates the (first) operator/predicate for this
	 * {@link ComposedFeatureClass}.
	 */
	private ClassOperatorType operator;

	/**
	 * In case of AND and OR the operand is a collection of two or more
	 * (Composed)FeatureClasses. In case of a unary operator the collection
	 * consists of just one (Composed)FeatureClass. Not strong-typed, this
	 * constraint must be dealt with by application code.
	 * 
	 */
	private List<FeatureClass> collection;

	// constructors ............................................................

	public ComposedFeatureClass(IAbout about) {
		super(about);
		// TODO Auto-generated constructor stub
	}

	// getters / setters .......................................................

	/**
	 * @return the collection
	 */
	public List<FeatureClass> getCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(List<FeatureClass> collection) {
		this.collection = collection;
	}

	/**
	 * @return the operator
	 */
	public ClassOperatorType getClassOperatorType() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setClassOperatorType(ClassOperatorType operator) {
		this.operator = operator;
	}

	public enum ClassOperatorType {
		AND, // intersection
		OR, // union
		NOT
	}

}
