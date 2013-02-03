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
import eu.esdihumboldt.hale.common.lookup.LookupService;
import eu.esdihumboldt.hale.common.lookup.LookupTableImport;

/**
 * Lookup table import advisor. Registers lookup tables with a
 * {@link LookupService}.
 * 
 * @author Simon Templer
 */
public class LookupImportAdvisor extends AbstractIOAdvisor<LookupTableImport> {

	@Override
	public void handleResults(LookupTableImport provider) {
		super.handleResults(provider);

		LookupService ls = getService(LookupService.class);
		ls.registerTable(provider.getResourceIdentifier(), provider.getLookupTable());
	}

}
