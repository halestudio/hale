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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.presets.extension;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Represents a schema category.
 * 
 * @author Simon Templer
 */
public interface SchemaCategory extends Identifiable, Named {

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public abstract String getId();

	/**
	 * @return the associated schemas
	 */
	public Iterable<SchemaPreset> getSchemas();

}