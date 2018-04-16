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

package eu.esdihumboldt.hale.io.groovy.ui.snippets;

import eu.esdihumboldt.hale.io.groovy.snippets.Snippet;
import eu.esdihumboldt.hale.io.groovy.snippets.SnippetService;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.action.AbstractActionUIAdvisor;

/**
 * UI advisor for snippets.
 * 
 * @author Simon Templer
 */
public class SnippetUIAdvisor extends AbstractActionUIAdvisor<Snippet> {

	@Override
	public Class<Snippet> getRepresentationType() {
		return Snippet.class;
	}

	@Override
	public boolean supportsRemoval() {
		return true;
	}

	@Override
	public boolean removeResource(String resourceId) {
		SnippetService ss = HaleUI.getServiceProvider().getService(SnippetService.class);
		if (ss != null) {
			ss.removeSnippet(resourceId);
			return true;
		}
		return false;
	}

	@Override
	public boolean supportsClear() {
		return true;
	}

	@Override
	public boolean clear() {
		SnippetService ss = HaleUI.getServiceProvider().getService(SnippetService.class);
		if (ss != null) {
			ss.clearSnippets();
			return true;
		}
		return false;
	}

	@Override
	public boolean supportsRetrieval() {
		return true;
	}

	@Override
	public Snippet retrieveResource(String resourceId) {
		SnippetService ss = HaleUI.getServiceProvider().getService(SnippetService.class);
		if (ss != null) {
			return ss.getResourceSnippet(resourceId).orElse(null);
		}
		return null;
	}

	@Override
	public boolean supportsCustomName() {
		return true;
	}

	@Override
	public String getCustomName(String resourceId) {
		Snippet snippet = retrieveResource(resourceId);
		if (snippet != null) {
			return snippet.getIdentifier();
		}
		return null;
	}

}
