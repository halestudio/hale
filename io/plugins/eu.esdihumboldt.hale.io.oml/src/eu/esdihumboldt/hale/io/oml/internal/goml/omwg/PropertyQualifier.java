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
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * A {@link PropertyQualifier} can be used to specifiy mappings between xml
 * attributes (while {@link Property} is for 'normal' properties, in case of
 * xml: xml elements).
 * 
 * @author Marian de Vries
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public class PropertyQualifier extends Entity {

	// private List<DomainRestriction> domainRestriction;
	private List<Property> domainRestriction;

	private List<String> typeCondition;

	// private List<ValueCondition> valueCondition;
	private List<Restriction> valueCondition;

	// constructors ............................................................

	/**
	 * @param label
	 */
	public PropertyQualifier(IAbout about) {
		super(about);
	}

	// FIXME copied from Entity, might not be applicable
	@Override
	public String getNamespace() {
		return this.getAbout().getAbout()
				.substring(0, (this.getAbout().getAbout().lastIndexOf("/")));
	}

	// getters / setters .......................................................

	/**
	 * Gets the value of the domainRestriction property.
	 * 
	 * Objects of the following type(s) are allowed in the list {@link Property }
	 * 
	 */
	public List<Property> getDomainRestriction() {
		if (domainRestriction == null) {
			domainRestriction = new ArrayList<Property>();
		}
		return this.domainRestriction;
	}

	/**
	 * Gets the value of the typeCondition property.
	 * 
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 */
	public List<String> getTypeCondition() {
		if (typeCondition == null) {
			typeCondition = new ArrayList<String>();
		}
		return this.typeCondition;
	}

	/**
	 * Gets the value of the valueCondition property.
	 * 
	 * Objects of the following type(s) are allowed in the list
	 * {@link Restriction }
	 * 
	 * 
	 */
	public List<Restriction> getValueCondition() {
		if (valueCondition == null) {
			valueCondition = new ArrayList<Restriction>();
		}
		return this.valueCondition;
	}

	@Override
	public IEntity deepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

}
