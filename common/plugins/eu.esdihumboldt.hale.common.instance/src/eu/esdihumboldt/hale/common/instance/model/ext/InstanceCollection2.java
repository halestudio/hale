/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.model.ext;

import java.util.Map;

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Extended instance collection adding fan-out capabilities.
 * 
 * @author Simon Templer
 */
public interface InstanceCollection2 extends InstanceCollection {

	/**
	 * States if the collection supports fan-out by type definition.
	 * 
	 * @return if type fan-out is supported
	 */
	public boolean supportsFanout();

	/**
	 * Fan-out the instance collection per occurring type. Should only be called
	 * if {@link #supportsFanout()} yields <code>true</code>.
	 * 
	 * @return the fanned out instance collections per type, or
	 *         <code>null</code>
	 */
	public Map<TypeDefinition, InstanceCollection> fanout();

}
