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

package eu.esdihumboldt.hale.common.schema.model.constraint.property;

import java.util.Collection;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Common interface for constraints representing references.
 * 
 * @author Simon Templer
 */
public interface ReferenceLike {

	/**
	 * Get the types of objects that may be associated through the reference.
	 * 
	 * @return the referenced types, may be <code>null</code> if unknown
	 */
	Collection<? extends TypeDefinition> getReferencedTypes();

	/**
	 * Add a referenced type. Marks the property explicitly as reference.
	 * 
	 * @param type the referenced type to add
	 */
	void addReferencedType(TypeDefinition type);

	/**
	 * Returns whether this reference references anything.
	 * 
	 * @return true, if this reference references anything, false otherwise
	 */
	boolean isReference();

}