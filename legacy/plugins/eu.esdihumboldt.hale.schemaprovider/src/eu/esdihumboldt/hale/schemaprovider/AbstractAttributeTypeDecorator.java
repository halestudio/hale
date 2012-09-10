/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.schemaprovider;

import java.util.List;
import java.util.Map;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.util.InternationalString;

/**
 * Abstract decorator for attribute types
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public abstract class AbstractAttributeTypeDecorator implements AttributeType {

	/**
	 * The decorated type
	 */
	protected final AttributeType type;

	/**
	 * Creates a decorator for the given attribute type
	 * 
	 * @param type
	 *            the attribute type
	 */
	protected AbstractAttributeTypeDecorator(AttributeType type) {
		super();

		this.type = type;
	}

	/**
	 * @see AttributeType#getSuper()
	 */
	@Override
	public AttributeType getSuper() {
		return type.getSuper();
	}

	/**
	 * @see AttributeType#isIdentified()
	 */
	@Override
	public boolean isIdentified() {
		return type.isIdentified();
	}

	/**
	 * @see PropertyType#getBinding()
	 */
	@Override
	public Class<?> getBinding() {
		return type.getBinding();
	}

	/**
	 * @see PropertyType#getDescription()
	 */
	@Override
	public InternationalString getDescription() {
		return type.getDescription();
	}

	/**
	 * @see PropertyType#getName()
	 */
	@Override
	public Name getName() {
		return type.getName();
	}

	/**
	 * @see PropertyType#getRestrictions()
	 */
	@Override
	public List<Filter> getRestrictions() {
		return type.getRestrictions();
	}

	/**
	 * @see PropertyType#getUserData()
	 */
	@Override
	public Map<Object, Object> getUserData() {
		return type.getUserData();
	}

	/**
	 * @see PropertyType#isAbstract()
	 */
	@Override
	public boolean isAbstract() {
		return type.isAbstract();
	}

}
