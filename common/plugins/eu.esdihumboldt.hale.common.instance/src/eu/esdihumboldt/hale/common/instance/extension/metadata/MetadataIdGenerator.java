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

package eu.esdihumboldt.hale.common.instance.extension.metadata;

import java.util.UUID;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * UniqueID Generator for Instance Metadatas
 * 
 * @author Sebastian Reinhardt
 */
public class MetadataIdGenerator implements MetadataGenerator {

	/**
	 * generates a unique Identifier for an instance
	 * 
	 * @param inst the instance
	 * @return an array with a unique ID inside
	 */
	@Override
	public Object[] generate(Instance inst) {
		return new Object[] { UUID.randomUUID().toString() };
	}

}
