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
package eu.esdihumboldt.hale.io.oml.internal.goml.align;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.FeatureClass;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Property;
import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.About;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * A {@link Cell} contains a mapping between two Entities, such as
 * {@link FeatureClass}es or {@link Property} objects.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public class Cell implements ICell {

	/**
	 * Annotation label(s) TODO check whether this and the IAbout aren't in
	 * conflict.
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
	 * The mapping/relation type between the two {@link Entity} objects. TODO
	 * replace by an extensible construct, maybe including the MDL. Note: MDL
	 * should go to schema of its own.
	 */
	private RelationType relation;

	/**
	 * The confidence as a numerical value of the correctness of this mapping
	 * TODO replace by MDL elements.
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

	@Override
	public IEntity getEntity1() {
		return entity1;
	}

	public void setEntity2(IEntity entity2) {
		this.entity2 = entity2;
	}

	@Override
	public IEntity getEntity2() {
		return entity2;
	}

	/**
	 * @return the relation
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
	public List<String> getLabel() {
		return label;
	}

	/**
	 * @param label the label(s) to set
	 */
	public void setLabel(List<String> label) {
		this.label = label;
	}

	public ICell deepCopy() {
		Cell result = new Cell();
		result.setAbout(new About(this.getAbout().getAbout()));

		result.setEntity1(((Entity) this.getEntity1()).deepCopy());
		result.setEntity2(((Entity) this.getEntity2()).deepCopy());

		List<String> newLabels = new ArrayList<String>();
		for (String label : this.getLabel()) {
			newLabels.add(label);
		}
		result.setLabel(newLabels);

		result.setMeasure(this.getMeasure());
		result.setRelation(this.getRelation()); // not yet deep

		return result;
	}

}
