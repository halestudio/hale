/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.io.oml.internal.goml.omwg;

import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This class represents omwg:relConst. Used for building an expression that
 * constructs a Relation based on other Relations.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
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
	 * @param collection the collection to set
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
	 * @param operator the operator to set
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
