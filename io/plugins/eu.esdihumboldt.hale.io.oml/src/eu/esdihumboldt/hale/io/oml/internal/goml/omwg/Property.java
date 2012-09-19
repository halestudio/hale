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
import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.DetailedAbout;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IParameter;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This class represents omwg:PropertyType.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public class Property extends Entity {

	/**
	 * <xs:element ref="omwg:domainRestriction" minOccurs="0"
	 * maxOccurs="unbounded" />
	 */
	private List<FeatureClass> domainRestriction;

	/**
	 * <xs:element ref="omwg:valueCondition" minOccurs="0" maxOccurs="unbounded"
	 * />
	 */
	private List<Restriction> valueCondition;

	/**
	 * TODO add explanation TODO: use actual geometry classes from GeoAPI
	 * instead of String.
	 * 
	 * <xs:element ref="omwg:typeCondition" minOccurs="0" maxOccurs="unbounded"
	 * />
	 */
	private List<String> typeCondition;

	// constructors ............................................................

	public Property(IAbout about) {
		super(about);
	}

	@Override
	public String getNamespace() {
		return DetailedAbout.getDetailedAbout(getAbout(), true).getNamespace();
	}

	public String getFeatureClassName() {
		return DetailedAbout.getDetailedAbout(getAbout(), true).getFeatureClass();
	}

	// getters/setters .........................................................

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
	 * @return the valueCondition
	 */
	public List<Restriction> getValueCondition() {
		return valueCondition;
	}

	/**
	 * @param valueCondition the valueCondition to set
	 */
	public void setValueCondition(List<Restriction> valueCondition) {
		this.valueCondition = valueCondition;
	}

	/**
	 * @return the typeCondition
	 */
	public List<String> getTypeCondition() {
		return typeCondition;
	}

	/**
	 * @param typeCondition the typeCondition to set
	 */
	public void setTypeCondition(List<String> typeCondition) {
		this.typeCondition = typeCondition;
	}

	@Override
	public IEntity deepCopy() {
		Property result = new Property(new About(this.getAbout().getAbout()));

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
