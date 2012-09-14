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
package eu.esdihumboldt.hale.io.oml.helper;

import java.util.Iterator;

import eu.esdihumboldt.hale.io.oml.internal.goml.align.Entity;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.ComposedProperty;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Property;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;

/**
 * Entity utility methods
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class EntityHelper {

	/**
	 * Get a short name for the given entity
	 * 
	 * @param entity the entity
	 * @return the short name
	 */
	public static String getShortName(IEntity entity) {
		if (entity.equals(Entity.NULL_ENTITY)) {
			return "None"; //$NON-NLS-1$
		}

		if (entity instanceof ComposedProperty) {
			ComposedProperty cp = (ComposedProperty) entity;
			Iterator<Property> it = cp.getCollection().iterator();
			StringBuffer result = new StringBuffer();
			while (it.hasNext()) {
				result.append(getShortName(it.next()));

				if (it.hasNext()) {
					result.append(" & "); //$NON-NLS-1$
				}
			}

			return result.toString();
		}

		if (entity.getAbout() != null && entity.getAbout().getAbout() != null) {
			String label = entity.getAbout().getAbout();
			String[] nameparts = label.split("\\/"); //$NON-NLS-1$
			if (entity instanceof Property && nameparts.length >= 2) {
				return nameparts[nameparts.length - 2] + "." + nameparts[nameparts.length - 1]; //$NON-NLS-1$
			}
			else {
				return nameparts[nameparts.length - 1];
			}
		}

		return "unnamed"; //$NON-NLS-1$
	}

//	/**
//	 * Get the identifier for the given entity, as in {@link Definition#getIdentifier()}
//	 * 
//	 * @param entity the entity
//	 * 
//	 * @return the entity's identifier
//	 */
//	public static String getIdentifier(IEntity entity) {
//		return entity.getAbout().getAbout();
//	}

}
