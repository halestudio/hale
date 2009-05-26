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
 * This class represents omwg:propConst. Used for building an expression that
 * constructs a Property based on other Entities (other Properties or Relations)
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class PropertyConstruction {

	/**
	 * <xs:group ref="omwg:propConst" minOccurs="0" maxOccurs="1" />
	 */
	private List<Property> and;
	private List<Property> or;
	private Property not;
	private Relation first;
	private Property next;
	
	// getters / setters .......................................................
	
	/**
	 * @return the and
	 */
	public List<Property> getAnd() {
		return and;
	}
	/**
	 * @param and the and to set
	 */
	public void setAnd(List<Property> and) {
		this.and = and;
	}
	/**
	 * @return the or
	 */
	public List<Property> getOr() {
		return or;
	}
	/**
	 * @param or the or to set
	 */
	public void setOr(List<Property> or) {
		this.or = or;
	}
	/**
	 * @return the not
	 */
	public Property getNot() {
		return not;
	}
	/**
	 * @param not the not to set
	 */
	public void setNot(Property not) {
		this.not = not;
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
	public Property getNext() {
		return next;
	}
	/**
	 * @param next the next to set
	 */
	public void setNext(Property next) {
		this.next = next;
	}

}
