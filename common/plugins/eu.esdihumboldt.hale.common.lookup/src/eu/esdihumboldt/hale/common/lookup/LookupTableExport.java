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

package eu.esdihumboldt.hale.common.lookup;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;

/**
 * Interface for the lookup table export
 * 
 * @author Patrick Lieb
 */
public interface LookupTableExport extends ExportProvider {

	/**
	 * Set the lookup table to export.
	 * 
	 * @param lookupTable the lookup table
	 * 
	 */
	public void setLookupTable(LookupTableInfo lookupTable);

	/**
	 * Get the lookup table to export.
	 * 
	 * @return the lookup Table
	 * 
	 */
	public LookupTableInfo getLookupTable();

}
