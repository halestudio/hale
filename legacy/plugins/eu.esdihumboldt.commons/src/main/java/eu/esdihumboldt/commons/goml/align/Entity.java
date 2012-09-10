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

package eu.esdihumboldt.commons.goml.align;

import java.util.List;

import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.rdf.About;
import eu.esdihumboldt.specification.cst.align.IEntity;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;
import eu.esdihumboldt.specification.cst.rdf.IAbout;

/**
 * {@link Entity} is the supertype for all objects that can be mapped in a
 * {@link Cell}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public abstract class Entity implements IEntity {

	/**
	 * Null entity
	 */
	public static Entity NULL_ENTITY = new FeatureClass(new About("entity",
			"null"));

	/**
	 * Note: Interior element omwg:label collapsed. <xs:element ref="omwg:label"
	 * minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<String> label;

	/**
	 * Note: this can be a single Function (transf) or a Service (service) or a
	 * pipe of transformations If defined on the Entity2, this represents an
	 * augmentation. <xs:group ref="omwg:transformation" minOccurs="0"
	 * maxOccurs="1" />
	 */
	private ITransformation transformation;

	/**
	 * Identifier of this {@link Entity} object
	 */
	private IAbout about;

	// constructors ............................................................

	/**
	 * @param label
	 */
	public Entity(IAbout about) {
		super();
		this.about = about;
	}

	/**
	 * @return the namespace part of this {@link Entity}.
	 */
	public abstract String getNamespace();

	/**
	 * @return the local name part of this {@link Entity}.
	 */
	public String getLocalname() {
		String[] nameparts = this.getAbout().getAbout().split("/");
		return nameparts[nameparts.length - 1];
	}

	// getters / setters .......................................................

	/**
	 * @return the label
	 */
	public List<String> getLabel() {
		return label;
	}

	/**
	 * @return the transformation
	 */
	public ITransformation getTransformation() {
		return transformation;
	}

	/**
	 * @param transformation
	 *            the transformation to set
	 */
	public void setTransformation(ITransformation transformation) {
		this.transformation = transformation;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(List<String> label) {
		this.label = label;
	}

	/**
	 * @return the about
	 */
	public IAbout getAbout() {
		return about;
	}

	/**
	 * @param about
	 *            the about to set
	 */
	public void setAbout(IAbout about) {
		this.about = about;
	}

	public String toString() {
		return this.getClass().getName() + ": " + this.getAbout().getAbout();
	}

	public abstract IEntity deepCopy();

}
