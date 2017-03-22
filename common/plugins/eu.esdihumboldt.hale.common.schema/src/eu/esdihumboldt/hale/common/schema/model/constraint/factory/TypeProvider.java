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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * Interface for type providers that allow resolving and populating types.
 * 
 * @author Simon Templer
 */
public interface TypeProvider extends TypeResolver {

	/**
	 * Get an existing type definition or create a new one. This is meant for
	 * the purpose of populating a type with information read from the schema.
	 * 
	 * @param typeName the type name
	 * @param id the identifier of the type
	 * @return the type definition to populate
	 */
	DefaultTypeDefinition getOrCreateType(QName typeName, Value id);

}
