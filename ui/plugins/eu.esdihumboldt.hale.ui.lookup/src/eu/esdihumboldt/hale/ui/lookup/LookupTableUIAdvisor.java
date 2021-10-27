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

package eu.esdihumboldt.hale.ui.lookup;

import eu.esdihumboldt.hale.common.lookup.LookupService;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.action.AbstractActionUIAdvisor;

/**
 * Lookup table UI advisor.
 * 
 * @author Simon Templer
 */
public class LookupTableUIAdvisor extends AbstractActionUIAdvisor<LookupTableInfo> {

	@Override
	public Class<LookupTableInfo> getRepresentationType() {
		return LookupTableInfo.class;
	}

	@Override
	public boolean supportsRemoval(String resourceId) {
		return true;
	}

	@Override
	public boolean removeResource(String resourceId) {
		return HaleUI.getServiceProvider().getService(LookupService.class).removeTable(resourceId);
	}

	@Override
	public boolean supportsRetrieval() {
		return true;
	}

	@Override
	public LookupTableInfo retrieveResource(String resourceId) {
		return HaleUI.getServiceProvider().getService(LookupService.class).getTable(resourceId);
	}

}
