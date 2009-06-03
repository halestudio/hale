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
public class RelationConstruction 
	extends Relation {

	/**
	 * <xs:group ref="omwg:relConst" minOccurs="0" maxOccurs="1" /> In stead of
	 * the group use the group members directly
	 */
	private List<FeatureClass> relations;

	private RelationConstructionType type;

	public RelationConstruction(List<String> label) {
		super(label);
		// TODO Auto-generated constructor stub
	}
	
	// getters / setters .......................................................
	

	/**
	 * @return the relations
	 */
	public List<FeatureClass> getRelations() {
		return relations;
	}

	/**
	 * @param relations the relations to set
	 */
	public void setRelations(List<FeatureClass> relations) {
		this.relations = relations;
	}

	/**
	 * @return the type
	 */
	public RelationConstructionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RelationConstructionType type) {
		this.type = type;
	}

	public enum RelationConstructionType {
		NOT,
		INVERSE,
		SYMMETRIC,
		TRANSITIVE,
		REFLEXIVE,
		FIRST,
		NEXT
	}
	
}
