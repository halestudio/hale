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
import eu.esdihumboldt.hale.common.lookup.LookupTableImport;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;

/**
 * TODO Type description
 * 
 * @author Patrick
 */
public class LookupLoadAdvisor extends AbstractIOAdvisor<LookupTableImport> {

	private LookupTableInfo lookupTable;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor#handleResults(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public void handleResults(LookupTableImport provider) {
		// TODO Auto-generated method stub
		super.handleResults(provider);
		lookupTable = provider.getLookupTable();
	}

	/**
	 * @return the lookupTable
	 */
	public LookupTableInfo getLookupTable() {
		return lookupTable;
	}
}
