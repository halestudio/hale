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

package eu.esdihumboldt.hale.common.lookup.impl;

import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.lookup.LookupTableExport;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;

/**
 * Abstract lookup table provider with basic functionality
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractLookupExport extends AbstractExportProvider implements
		LookupTableExport {

	private LookupTableInfo lookupTable;

	/**
	 * @see eu.esdihumboldt.hale.common.lookup.LookupTableExport#getLookupTable()
	 */
	@Override
	public LookupTableInfo getLookupTable() {
		return lookupTable;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.lookup.LookupTableExport#setLookupTable(eu.esdihumboldt.hale.common.lookup.LookupTableInfo)
	 */
	@Override
	public void setLookupTable(LookupTableInfo lookupTable) {
		this.lookupTable = lookupTable;

	}

}
