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

package eu.esdihumboldt.hale.io.oml.internal.goml.align;

import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.FeatureClass;
import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.About;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.ITransformation;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * {@link Entity} is the supertype for all objects that can be mapped in a
 * {@link Cell}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public abstract class Entity implements IEntity {

	/**
	 * Null entity
	 */
	public static Entity NULL_ENTITY = new FeatureClass(new About("entity", "null"));

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
	@Override
	public List<String> getLabel() {
		return label;
	}

	/**
	 * @return the transformation
	 */
	@Override
	public ITransformation getTransformation() {
		return transformation;
	}

	/**
	 * @param transformation the transformation to set
	 */
	public void setTransformation(ITransformation transformation) {
		this.transformation = transformation;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(List<String> label) {
		this.label = label;
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

	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.getAbout().getAbout();
	}

	public abstract IEntity deepCopy();

}
