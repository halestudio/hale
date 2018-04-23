/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.service.align.resolver.internal;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;

/**
 * Simple cache for resolved / replaced entities.
 * 
 * @author Simon Templer
 */
public class ResolveCache {

	private final Map<TypeEntityDefinition, TypeEntityDefinition> typeCache = new HashMap<>();

	private final Map<PropertyEntityDefinition, PropertyEntityDefinition> propertyCache = new HashMap<>();

	/**
	 * Set the replacement for the given original entity
	 * 
	 * @param original the original entity
	 * @param replacement the replacement entity
	 */
	public void put(TypeEntityDefinition original, TypeEntityDefinition replacement) {
		typeCache.put(original, replacement);
	}

	/**
	 * Get the replacement for the given original entity
	 * 
	 * @param original the original entity
	 * @return the replacement entity or <code>null</code>
	 */
	public TypeEntityDefinition getReplacement(TypeEntityDefinition original) {
		return typeCache.get(original);
	}

	/**
	 * Set the replacement for the given original entity
	 * 
	 * @param original the original entity
	 * @param replacement the replacement entity
	 */
	public void put(PropertyEntityDefinition original, PropertyEntityDefinition replacement) {
		propertyCache.put(original, replacement);
	}

	/**
	 * Get the replacement for the given original entity
	 * 
	 * @param original the original entity
	 * @return the replacement entity or <code>null</code>
	 */
	public PropertyEntityDefinition getReplacement(PropertyEntityDefinition original) {
		return propertyCache.get(original);
	}

}
