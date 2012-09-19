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
