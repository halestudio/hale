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


/**
 * This class represents the <xs:complexType name="RelationType">, to be used when a relation between (feature)classes is mapped. 
 * Not to be confused with the Java enum type RelationType, 
 *   which is a list of possible semantic relations between Entities in an OML Cell.
 * 
 * @author Marian de Vries 
 * @partner 08 / Delft University of Technology
 * @version $Id$ 
 */
public class Relation 
	extends Entity {
	
	
	/**
	 * <xs:element ref="omwg:label" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<String> label;

	
	/**
	 * <xs:group ref="omwg:relConst" minOccurs="0" maxOccurs="1" />
       * In stead of the group use the group members directly
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

	
	/**
	 * <xs:group ref="omwg:relCond" minOccurs="0" maxOccurs="unbounded" />
       * In stead of the group use the group members directly
	 */

	/**
	 * <xs:element ref="omwg:domainRestriction" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<FeatureClass> domainRestriction;

	/**
	 * <xs:element ref="omwg:rangeRestriction" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<FeatureClass> rangeRestriction;
	
	// constructors ............................................................
	
	public Relation(List<String> labels) {
		super(labels);
	}

}
