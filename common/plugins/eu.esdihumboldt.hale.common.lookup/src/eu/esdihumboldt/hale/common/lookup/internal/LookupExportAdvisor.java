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

package eu.esdihumboldt.hale.common.lookup.internal;

import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.lookup.LookupTableExport;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;

/**
 * Export advisor for lookup tables
 * 
 * @author Patrick Lieb
 */
public class LookupExportAdvisor extends AbstractIOAdvisor<LookupTableExport> {

	private final LookupTableInfo lookupTable;

	/**
	 * @param lookupTable the lookup table
	 * 
	 */
	public LookupExportAdvisor(LookupTableInfo lookupTable) {
		this.lookupTable = lookupTable;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor#prepareProvider(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public void prepareProvider(LookupTableExport provider) {
		super.prepareProvider(provider);
		provider.setLookupTable(lookupTable);
	}
}
