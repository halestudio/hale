/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.groovy.snippets.impl;

import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.io.groovy.snippets.SnippetReader;
import eu.esdihumboldt.hale.io.groovy.snippets.SnippetService;

/**
 * Advisor for loading snippets.
 * 
 * @author Simon Templer
 */
public class SnippetAdvisor extends DefaultIOAdvisor<SnippetReader> {

	@Override
	public void handleResults(SnippetReader provider) {
		super.handleResults(provider);

		SnippetService snippets = getService(SnippetService.class);
		snippets.addSnippet(provider.getResourceIdentifier(), provider.getSnippet());
	}

}
