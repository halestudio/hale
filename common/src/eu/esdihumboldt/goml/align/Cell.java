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
package eu.esdihumboldt.goml.align;

import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

/**
 * A Cell contains a mapping between two Entities, such as {@link FeatureClass}es
 * or {@link Property} objects.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Cell {

	/**
	 * The first {@link Entity} mapped by this {@link Cell}.
	 */
	private Entity entity1;
	
	/**
	 * The second {@link Entity} mapped by this {@link Cell}.
	 */
	private Entity entity2;
	
	/**
	 * The mapping/relation type between the two {@link Entity} objects.
	 */
	private RelationType relation;
	
	/**
	 * TODO add comment
	 */
	private Measure measure;
	
	/**
	 * Metadata on this {@link Cell}.
	 */
	private About about;
	
	
	// getters/setters .........................................................
	
	public void setEntity1(Entity entity1) {
		this.entity1 = entity1;
	}

	public Entity getEntity1() {
		return entity1;
	}
	
	public void setEntity2(Entity entity2) {
		this.entity2 = entity2;
	}

	public Entity getEntity2() {
		return entity2;
	}
	
	/**
	 * @return the relation
	 */
	public RelationType getRelation() {
		return relation;
	}

	/**
	 * @param relation the relation to set
	 */
	public void setRelation(RelationType relation) {
		this.relation = relation;
	}

	/**
	 * @return the measure
	 */
	public Measure getMeasure() {
		return measure;
	}

	/**
	 * @param measure the measure to set
	 */
	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	/**
	 * @return the about
	 */
	public About getAbout() {
		return about;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(About about) {
		this.about = about;
	}

	public enum RelationType {
		Equivalence,
		Subsumes,
		SubsumedBy,
		InstanceOf,
		HasInstance,
		Disjoint,
		PartOf, // TODO, might have to go elsewhere. added by MdV
		Extra, // TODO, might have to go elsewhere. added by MdV
		Missing // TODO, might have to go elsewhere. added by MdV
	}
}
