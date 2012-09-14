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
