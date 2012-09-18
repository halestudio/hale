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

package eu.esdihumboldt.hale.common.schema.model;

import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;

/**
 * A schema is a set of type definitions originating from the same source.
 * 
 * @author Simon Templer
 */
public interface Schema extends TypeIndex, Locatable {

	/**
	 * Get the schema namespace
	 * 
	 * @return the namespace
	 */
	public String getNamespace();

}
