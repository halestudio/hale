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

package eu.esdihumboldt.hale.common.align.io;

import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Provides support for reading an alignment
 * 
 * @author Simon Templer
 */
public interface AlignmentReader extends ImportProvider {

	/**
	 * Set the source schema
	 * 
	 * @param sourceSchema the source schema
	 */
	public void setSourceSchema(TypeIndex sourceSchema);

	/**
	 * Set the target schema
	 * 
	 * @param targetSchema the source schema
	 */
	public void setTargetSchema(TypeIndex targetSchema);

	/**
	 * Set the path updater
	 * 
	 * @param updater the path updater
	 */
	public void setPathUpdater(PathUpdate updater);

	/**
	 * Get the loaded alignment
	 * 
	 * @return the loaded alignment
	 */
	public MutableAlignment getAlignment();

}
