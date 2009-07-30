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

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;

import eu.esdihumboldt.cst.rdf.IAbout;

import java.util.List;

/**
 * A {@link Cell} contains a mapping between two Entities, such as {@link FeatureClass}es
 * or {@link Property} objects.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Cell 
	implements ICell {

	/**
	 * Annotation label(s)
	 */
	private List<String> label;

	/**
	 * The first {@link Entity} mapped by this {@link Cell}.
	 */
	private IEntity entity1;
	
	/**
	 * The second {@link Entity} mapped by this {@link Cell}.
	 */
	private IEntity entity2;
	
	/**
	 * The mapping/relation type between the two {@link Entity} objects.
	 */
	private RelationType relation;
	
	/**
	 * TODO add comment
	 */
	private double measure;
	
	/**
	 * Identifier of this {@link Cell} (optional).
	 */
	private IAbout about;
	
	
	// getters/setters .........................................................
	
	public void setEntity1(IEntity entity1) {
		this.entity1 = entity1;
	}

	public IEntity getEntity1() {
		return entity1;
	}
	
	public void setEntity2(IEntity entity2) {
		this.entity2 = entity2;
	}

	public IEntity getEntity2() {
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
	public double getMeasure() {
		return measure;
	}

	/**
	 * @param measure the measure to set
	 */
	public void setMeasure(double measure) {
		this.measure = measure;
	}

	/**
	 * @return the about
	 */
	public IAbout getAbout() {
		return about;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(IAbout about) {
		this.about = about;
	}

	/**
	 * @return the label(s)
	 */
	public List<String> getLabel() {
		return label;
	}

	/**
	 * @param label the label(s) to set
	 */
	public void setLabel(List<String> label) {
		this.label = label;
	}

}
