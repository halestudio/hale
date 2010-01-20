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

package eu.esdihumboldt.hale.rcp.wizards.augmentations;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * Schema item representing the {@link Entity#NULL_ENTITY}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NullSchemaItem implements SchemaItem {
	
	/**
	 * The null schema item instance
	 */
	public static final SchemaItem INSTANCE = new NullSchemaItem();
	
	private final Name name = new NameImpl(
			Entity.NULL_ENTITY.getNamespace(), 
			Entity.NULL_ENTITY.getLocalname());
	
	/**
	 * Default constructor
	 */
	private NullSchemaItem() {
		super();
	}

	/**
	 * @see SchemaItem#getChildren()
	 */
	@Override
	public SchemaItem[] getChildren() {
		return null;
	}

	/**
	 * @see SchemaItem#getEntity()
	 */
	@Override
	public Entity getEntity() {
		return Entity.NULL_ENTITY;
	}

	/**
	 * @see SchemaItem#getName()
	 */
	@Override
	public Name getName() {
		return name;
	}

	/**
	 * @see SchemaItem#getParent()
	 */
	@Override
	public SchemaItem getParent() {
		return null;
	}

	/**
	 * @see SchemaItem#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/**
	 * @see SchemaItem#isAttribute()
	 */
	@Override
	public boolean isAttribute() {
		return false;
	}

	/**
	 * @see SchemaItem#isFeatureType()
	 */
	@Override
	public boolean isFeatureType() {
		return false;
	}

	/**
	 * @see SchemaItem#isType()
	 */
	@Override
	public boolean isType() {
		return false;
	}

	/**
	 * @see SchemaItem#getType()
	 */
	@Override
	public TreeObjectType getType() {
		return null;
	}

	/**
	 * @see SchemaItem#getPropertyType()
	 */
	@Override
	public PropertyType getPropertyType() {
		return null;
	}

	

}
