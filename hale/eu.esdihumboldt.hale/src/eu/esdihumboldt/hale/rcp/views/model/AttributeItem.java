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

package eu.esdihumboldt.hale.rcp.views.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.feature.NameImpl;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.cst.rdf.IAbout;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.DetailedAbout;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;

/**
 * Schema item representing a property
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class AttributeItem extends TreeParent {
	
	private static final Logger log = Logger.getLogger(AttributeItem.class);
	
	private final AttributeDefinition attributeDefinition;

	/**
	 * Creates a property item
	 * 
	 * @param attribute the attribute definition
	 */
	public AttributeItem(AttributeDefinition attribute) {
		super(
				attribute.getName() + ":<" + //$NON-NLS-1$
					attribute.getAttributeType().getDisplayName() + ">",  //$NON-NLS-1$
				new NameImpl(attribute.getDeclaringType().getIdentifier(), attribute.getName()), 
				determineType(attribute), 
				attribute.getAttributeType().getType(null));
		
		this.attributeDefinition = attribute;
	}

	/**
	 * Determine the {@link TreeObject.TreeObjectType} for a property
	 *   descriptor
	 * 
	 * @param attribute the attribute definition
	 * 
	 * @return the tree object type
	 */
	private static TreeObjectType determineType(AttributeDefinition attribute) {
		PropertyType type = attribute.getAttributeType().getType(null);
		Class<?> binding = type.getBinding();
		
		if (type.toString().matches("^.*?GMLComplexTypes.*")) { //$NON-NLS-1$
//		if (pd.getType().getName().getNamespaceURI().equals("http://www.opengis.net/gml")) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (org.opengis.feature.type.GeometryType.class.isAssignableFrom(binding)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (com.vividsolutions.jts.geom.Geometry.class.isAssignableFrom(binding)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (com.vividsolutions.jts.geom.Puntal.class.isAssignableFrom(binding)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (com.vividsolutions.jts.geom.Polygonal.class.isAssignableFrom(binding)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (com.vividsolutions.jts.geom.Lineal.class.isAssignableFrom(binding)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		}
		// numeric
		else if (Number.class.isAssignableFrom(binding) || Date.class.isAssignableFrom(binding)) {
			return TreeObjectType.NUMERIC_ATTRIBUTE;
		}
		// string
		else if (String.class.isAssignableFrom(binding)) {
			return TreeObjectType.STRING_ATTRIBUTE;
		}
		// boolean
		else if (Boolean.class.isAssignableFrom(binding)) {
			return TreeObjectType.STRING_ATTRIBUTE; //TODO new attribute type?
		}
		// default geometry attribute
		else if (attribute.getName().equalsIgnoreCase("geometry") || //$NON-NLS-1$
				attribute.getName().equalsIgnoreCase("the_geom")) { //$NON-NLS-1$
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		}
		// default geographical name attribute
		else if (attribute.getName().equals("GeographicalName")) { //$NON-NLS-1$
			return TreeObjectType.GEOGRAPHICAl_NAME_ATTRIBUTE;
		}
		else if (Arrays.asList(type.getClass().getInterfaces())
				.contains(org.opengis.feature.type.ComplexType.class)) {
			return TreeObjectType.COMPLEX_ATTRIBUTE;
		}
		// collection
		else if (Collection.class.isAssignableFrom(binding)) {
			return TreeObjectType.COMPLEX_ATTRIBUTE;
		}
	
		// default to complex attribute
		return TreeObjectType.COMPLEX_ATTRIBUTE;
	}

	/**
	 * @return the attributeDefinition
	 */
	public AttributeDefinition getAttributeDefinition() {
		return attributeDefinition;
	}

	/**
	 * @see SchemaItem#getDefinition()
	 */
	@Override
	public Definition getDefinition() {
		return attributeDefinition;
	}

	/**
	 * @see TreeObject#getEntity()
	 */
	@Override
	public Entity getEntity() {
		// don't use definition entity, because we want the whole property path
		List<String> properties = new ArrayList<String>();
		
		SchemaItem current = this;
		while (current != null && current.isAttribute()) {
			properties.add(0, current.getName().getLocalPart());
			
			current = current.getParent();
		}
		
		if (current != null && current.isType()) {
			IAbout about = new DetailedAbout(current.getName().getNamespaceURI(), 
					current.getName().getLocalPart(), properties);
			
			return new Property(about);
		}
		else {
			log.error("Error creating property entity: invalid schema item hierarchy, using definition entity instead");
			return super.getEntity();
		}
	}
}
