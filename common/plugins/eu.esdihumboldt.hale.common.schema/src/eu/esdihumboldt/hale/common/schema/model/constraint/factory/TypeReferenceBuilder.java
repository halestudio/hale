/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import java.util.Optional;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Interface for creating type references.
 * 
 * @author Simon Templer
 */
@FunctionalInterface
public interface TypeReferenceBuilder {

	/**
	 * Create a reference to the given type definition.
	 * 
	 * @param type the type definition
	 * @return the type reference if a reference can be created
	 */
	Optional<Value> createReference(TypeDefinition type);

}
