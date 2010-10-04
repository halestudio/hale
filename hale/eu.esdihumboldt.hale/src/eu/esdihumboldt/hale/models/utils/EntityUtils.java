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

package eu.esdihumboldt.hale.models.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class EntityUtils {

	/**
	 * Determine if two non-null entities match
	 * 
	 * @param e1 the first entity
	 * @param e2 the second entity
	 * @return if the entities match
	 */
	public static boolean entitiesMatch(IEntity e1, IEntity e2) {
		if (e1 != null && e2 != null) {
			String about1 = e1.getAbout().getAbout();
			String about2 = e2.getAbout().getAbout();
			
			if (about1.equals(about2)) {
				return true;
			}
			else {
				// about doesn't match
				
				// check composed properties
				if (e1 instanceof ComposedProperty && e2 instanceof ComposedProperty) {
					// both entities are composed properties
					return compositionsMatch((ComposedProperty) e1, (ComposedProperty) e2);
				}
				else if (e1 instanceof ComposedProperty && e2 instanceof Property) {
					// e1 is composed, e2 is a property
					return compositionContains((ComposedProperty) e1, (Property) e2);
				}
				else if (e1 instanceof Property && e2 instanceof ComposedProperty) {
					// e1 is property, e2 is composed
					return compositionContains((ComposedProperty) e2, (Property) e1);
				}
				//TODO check composed feature types
				
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * Determine if two composed properties match
	 * 
	 * @param c1 the first composed property
	 * @param c2 the second composed property
	 * 
	 * @return if both compositions contain the same properties
	 */
	private static boolean compositionsMatch(ComposedProperty c1, ComposedProperty c2) {
		boolean c2Empty = c2.getCollection() == null || c2.getCollection().isEmpty();
		
		if (c1.getCollection() == null || c1.getCollection().isEmpty()) {
			return c2Empty;
		}
		else {
			if (c2Empty) {
				return false;
			}
			
			List<Property> c2Properties = new ArrayList<Property>(c2.getCollection());
			
			for (Property c1Property : c1.getCollection()) {
				Property match = null;
				
				Iterator<Property> itC2 = c2Properties.iterator();
				while (itC2.hasNext() && match == null) {
					Property c2Property = itC2.next();
					
					if (entitiesMatch(c1Property, c2Property)) {
						match = c2Property;
					}
				}
				
				if (match == null) {
					// no match for a property found
					return false;
				}
				else {
					c2Properties.remove(match);
				}
			}
			
			// all properties were matched
			// if the collection is empty there are no additional properties in c2
			return c2Properties.isEmpty();
		}
	}

	/**
	 * Determine if the given composed property contains the given property
	 * 
	 * @param composition the composed property
	 * @param property the property
	 * 
	 * @return if the property is contained in the composition
	 */
	private static boolean compositionContains(ComposedProperty composition, Property property) {
		if (composition.getCollection() != null) {
			for (Property candidate : composition.getCollection()) {
				if (entitiesMatch(candidate, property)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
