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

import java.util.List;

/**
 * This class represents omwg:relConst. Used for building an expression that
 * constructs a Relation based on other Entities (Classes or other Relations).
 * 
 * @author Marian de Vries
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class RelationConstruction {

	/**
	 * <xs:group ref="omwg:relConst" minOccurs="0" maxOccurs="1" /> In stead of
	 * the group use the group members directly
	 */
	private List<FeatureClass> and;
	private List<FeatureClass> or;
	private Relation not;
	private Relation inverse;
	private Relation symmetric;
	private Relation transitive;
	private Relation reflexive;
	private Relation first;
	private Relation next;
	
	// getters / setters .......................................................
	
	/**
	 * @return the and
	 */
	public List<FeatureClass> getAnd() {
		return and;
	}
	/**
	 * @param and the and to set
	 */
	public void setAnd(List<FeatureClass> and) {
		this.and = and;
	}
	/**
	 * @return the or
	 */
	public List<FeatureClass> getOr() {
		return or;
	}
	/**
	 * @param or the or to set
	 */
	public void setOr(List<FeatureClass> or) {
		this.or = or;
	}
	/**
	 * @return the not
	 */
	public Relation getNot() {
		return not;
	}
	/**
	 * @param not the not to set
	 */
	public void setNot(Relation not) {
		this.not = not;
	}
	/**
	 * @return the inverse
	 */
	public Relation getInverse() {
		return inverse;
	}
	/**
	 * @param inverse the inverse to set
	 */
	public void setInverse(Relation inverse) {
		this.inverse = inverse;
	}
	/**
	 * @return the symmetric
	 */
	public Relation getSymmetric() {
		return symmetric;
	}
	/**
	 * @param symmetric the symmetric to set
	 */
	public void setSymmetric(Relation symmetric) {
		this.symmetric = symmetric;
	}
	/**
	 * @return the transitive
	 */
	public Relation getTransitive() {
		return transitive;
	}
	/**
	 * @param transitive the transitive to set
	 */
	public void setTransitive(Relation transitive) {
		this.transitive = transitive;
	}
	/**
	 * @return the reflexive
	 */
	public Relation getReflexive() {
		return reflexive;
	}
	/**
	 * @param reflexive the reflexive to set
	 */
	public void setReflexive(Relation reflexive) {
		this.reflexive = reflexive;
	}
	/**
	 * @return the first
	 */
	public Relation getFirst() {
		return first;
	}
	/**
	 * @param first the first to set
	 */
	public void setFirst(Relation first) {
		this.first = first;
	}
	/**
	 * @return the next
	 */
	public Relation getNext() {
		return next;
	}
	/**
	 * @param next the next to set
	 */
	public void setNext(Relation next) {
		this.next = next;
	}

}
