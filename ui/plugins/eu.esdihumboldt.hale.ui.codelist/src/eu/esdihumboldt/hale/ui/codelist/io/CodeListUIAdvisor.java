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

package eu.esdihumboldt.hale.ui.codelist.io;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.ui.codelist.service.CodeListService;
import eu.esdihumboldt.hale.ui.io.action.AbstractActionUIAdvisor;

/**
 * Code list action UI advisor.
 * 
 * @author Simon Templer
 */
public class CodeListUIAdvisor extends AbstractActionUIAdvisor<CodeList> {

	@Override
	public Class<CodeList> getRepresentationType() {
		return CodeList.class;
	}

	@Override
	public boolean supportsRemoval(String resourceId) {
		return true;
	}

	@Override
	public boolean removeResource(String resourceId) {
		CodeListService cs = PlatformUI.getWorkbench().getService(CodeListService.class);
		return cs.removeCodeList(resourceId);
	}

	@Override
	public boolean supportsRetrieval() {
		return true;
	}

	@Override
	public CodeList retrieveResource(String resourceId) {
		CodeListService cs = PlatformUI.getWorkbench().getService(CodeListService.class);
		return cs.getCodeList(resourceId);
	}

}
