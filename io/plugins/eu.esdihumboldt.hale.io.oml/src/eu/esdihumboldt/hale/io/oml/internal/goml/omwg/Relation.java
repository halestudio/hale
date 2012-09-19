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

import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.goml.align.Entity;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This class represents the <xs:complexType name="RelationType">, to be used
 * when a relation between (feature)classes is mapped. Not to be confused with
 * the Java enum type {@link RelationType}, which is a list of possible semantic
 * relations between Entities in an OML Cell.
 * 
 * @author Marian de Vries
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public class Relation extends Entity {

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

	public Relation(IAbout about) {
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

	@Override
	public IEntity deepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

}
