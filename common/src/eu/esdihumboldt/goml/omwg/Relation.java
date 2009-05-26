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

import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.align.Cell.RelationType;

/**
 * This class represents the <xs:complexType name="RelationType">, to be used
 * when a relation between (feature)classes is mapped. Not to be confused with
 * the Java enum type {@link RelationType}, which is a list of possible semantic
 * relations between Entities in an OML Cell.
 * 
 * @author Marian de Vries
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */
public class Relation extends Entity {

	/**
	 * <xs:group ref="omwg:relConst" minOccurs="0" maxOccurs="1" />
	 */
	private RelationConstruction relConstruction;

	/**
	 * <xs:group ref="omwg:relCond" minOccurs="0" maxOccurs="unbounded" /> In
	 * stead of the group use the group members directly
	 */

	/**
	 * <xs:element ref="omwg:domainRestriction" minOccurs="0"
	 * maxOccurs="unbounded" />
	 */
	private List<FeatureClass> domainRestriction;

	/**
	 * <xs:element ref="omwg:rangeRestriction" minOccurs="0"
	 * maxOccurs="unbounded" />
	 */
	private List<FeatureClass> rangeRestriction;

	// constructors ............................................................

	public Relation(List<String> label) {
		super(label);
	}
	
	// getters / setters .......................................................

	/**
	 * @return the relConstruction
	 */
	public RelationConstruction getRelConstruction() {
		return relConstruction;
	}

	/**
	 * @param relConstruction the relConstruction to set
	 */
	public void setRelConstruction(RelationConstruction relConstruction) {
		this.relConstruction = relConstruction;
	}

	/**
	 * @return the domainRestriction
	 */
	public List<FeatureClass> getDomainRestriction() {
		return domainRestriction;
	}

	/**
	 * @param domainRestriction the domainRestriction to set
	 */
	public void setDomainRestriction(List<FeatureClass> domainRestriction) {
		this.domainRestriction = domainRestriction;
	}

	/**
	 * @return the rangeRestriction
	 */
	public List<FeatureClass> getRangeRestriction() {
		return rangeRestriction;
	}

	/**
	 * @param rangeRestriction the rangeRestriction to set
	 */
	public void setRangeRestriction(List<FeatureClass> rangeRestriction) {
		this.rangeRestriction = rangeRestriction;
	}
	
	

}
