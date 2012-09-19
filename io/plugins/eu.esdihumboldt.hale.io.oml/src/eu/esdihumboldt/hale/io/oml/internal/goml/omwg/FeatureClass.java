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
package eu.esdihumboldt.hale.io.oml.internal.goml.omwg;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.goml.align.Entity;
import eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext.Parameter;
import eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext.Transformation;
import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.About;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IParameter;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This class represents the <xs:complexType name="ClassType">. Some interior
 * types have been collapsed to keep the number of classes to the required
 * minimum.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public class FeatureClass extends Entity {

	/**
	 * Note: Interior element omwg:classConditionType collapsed. <xs:element
	 * ref="omwg:attributeValueCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeValueCondition;

	/**
	 * Note: Interior element omwg:classConditionType collapsed. <xs:element
	 * Re="omwg:attributeTypeCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeTypeCondition;

	/**
	 * Note: Interior element omwg:classConditionType collapsed. <xs:element
	 * ref="omwg:attributeOccurenceCondition" minOccurs="0"
	 * maxOccurs="unbounded" />
	 */
	private List<Restriction> attributeOccurenceCondition;

	// constructors ............................................................

	/**
	 * @param label
	 */
	public FeatureClass(IAbout about) {
		super(about);
	}

	@Override
	public String getNamespace() {
		return this.getAbout().getAbout()
				.substring(0, (this.getAbout().getAbout().lastIndexOf("/")));
	}

	// getters / setters .......................................................

	/**
	 * @return the attributeValueCondition
	 */
	public List<Restriction> getAttributeValueCondition() {
		return attributeValueCondition;
	}

	/**
	 * @param attributeValueCondition the attributeValueCondition to set
	 */
	public void setAttributeValueCondition(List<Restriction> attributeValueCondition) {
		this.attributeValueCondition = attributeValueCondition;
	}

	/**
	 * @return the attributeTypeCondition
	 */
	public List<Restriction> getAttributeTypeCondition() {
		return attributeTypeCondition;
	}

	/**
	 * @param attributeTypeCondition the attributeTypeCondition to set
	 */
	public void setAttributeTypeCondition(List<Restriction> attributeTypeCondition) {
		this.attributeTypeCondition = attributeTypeCondition;
	}

	/**
	 * @return the attributeOccurenceCondition
	 */
	public List<Restriction> getAttributeOccurenceCondition() {
		return attributeOccurenceCondition;
	}

	/**
	 * @param attributeOccurenceCondition the attributeOccurenceCondition to set
	 */
	public void setAttributeOccurenceCondition(List<Restriction> attributeOccurenceCondition) {
		this.attributeOccurenceCondition = attributeOccurenceCondition;
	}

	@Override
	public IEntity deepCopy() {
		FeatureClass result = new FeatureClass(new About(this.getAbout().getAbout()));

		Transformation t = new Transformation(this.getTransformation().getService());
		List<IParameter> parameters = new ArrayList<IParameter>();
		for (IParameter p : this.getTransformation().getParameters()) {
			parameters.add(new Parameter(p.getName(), p.getValue()));
		}
		t.setParameters(parameters);
		result.setTransformation(t);

		List<String> newLabels = new ArrayList<String>();
		for (String label : this.getLabel()) {
			newLabels.add(label);
		}
		result.setLabel(newLabels);

		return result;
	}

}
