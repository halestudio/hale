/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.headless.impl;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.config.CodeListReference;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.codelist.service.CodeListRegistry;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import groovy.transform.CompileStatic

/**
 * Advisor for loading code lists in headless mode.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class CodeListAdvisor extends AbstractIOAdvisor<CodeListReader> implements CodeListRegistry {

	private final Map<CodeListReference, CodeList> codeLists = new HashMap<>();

	@Override
	public void handleResults(CodeListReader provider) {
		CodeList codeList = provider.getCodeList();

		if (codeList != null) {
			CodeListReference key = new CodeListReference(namespace: codeList.getNamespace(),
			identifier: codeList.getIdentifier());
		}
	}

	@Override
	public CodeList findCodeList(CodeListReference clRef) {
		return codeLists.get(clRef);
	}
}
