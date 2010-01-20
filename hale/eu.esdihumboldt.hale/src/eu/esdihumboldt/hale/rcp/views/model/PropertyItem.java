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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

/**
 * Schema item representing a property
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PropertyItem extends TreeParent {

	/**
	 * Creates a property item
	 * 
	 * @param propertyDescriptor the feature type
	 */
	public PropertyItem(PropertyDescriptor propertyDescriptor) {
		super(
				propertyDescriptor.getName().getLocalPart() + ":<" +
					propertyDescriptor.getType().getName().getLocalPart() + ">", 
				propertyDescriptor.getName(), 
				determineType(propertyDescriptor), 
				propertyDescriptor.getType());
	}

	/**
	 * Determine the {@link TreeObject.TreeObjectType} for a property
	 *   descriptor
	 * 
	 * @param pd the property descriptor
	 * 
	 * @return the tree object type
	 */
	private static TreeObjectType determineType(PropertyDescriptor pd) {
		PropertyType type = pd.getType();
		Class<?> binding = type.getBinding();
		
		if (type.toString().matches("^.*?GMLComplexTypes.*")) {
//		if (pd.getType().getName().getNamespaceURI().equals("http://www.opengis.net/gml")) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (Arrays.asList(binding.getClass().getInterfaces())
				.contains(org.opengis.feature.type.GeometryType.class)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (checkInterface(binding.getInterfaces(),
				com.vividsolutions.jts.geom.Puntal.class)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (checkInterface(binding.getInterfaces(),
				com.vividsolutions.jts.geom.Polygonal.class)) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
		} else if (checkInterface(binding.getInterfaces(),
				com.vividsolutions.jts.geom.Lineal.class)) {
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
		else if (pd.getName().getLocalPart().equalsIgnoreCase("geometry") ||
				pd.getName().getLocalPart().equalsIgnoreCase("the_geom")) {
			return TreeObjectType.GEOMETRIC_ATTRIBUTE;
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
	
	@SuppressWarnings("unchecked")
	private static boolean checkInterface(Class<?>[] classes, Class classToFind) {
		for (Class clazz : classes) {
			if (clazz.equals(classToFind)) return true;
			for (Class c : clazz.getInterfaces()) {
				if (c.equals(classToFind)) return true;
			}
		}
		return false;
	}

}
