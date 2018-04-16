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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import eu.esdihumboldt.hale.io.groovy.snippets.Snippet;
import eu.esdihumboldt.hale.io.groovy.snippets.SnippetService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyServiceListener;

/**
 * Default snippet service implementation.
 * 
 * @author Simon Templer
 */
public class SnippetServiceImpl implements SnippetService {

	private final Map<String, Snippet> snippets = new HashMap<>();
	private final Map<String, Snippet> byId = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param gs the groovy service
	 */
	public SnippetServiceImpl(GroovyService gs) {
		super();

		gs.addListener(new GroovyServiceListener() {

			@Override
			public void restrictionChanged(boolean restrictionActive) {
				synchronized (snippets) {
					snippets.values().forEach(snippet -> snippet.invalidate());
				}
			}
		});
	}

	@Override
	public void addSnippet(String resourceId, Snippet snippet) {
		if (snippet == null) {
			return;
		}
		synchronized (snippets) {
			snippets.put(resourceId, snippet);
			byId.put(snippet.getIdentifier(), snippet);
		}
	}

	@Override
	public void removeSnippet(String resourceId) {
		synchronized (snippets) {
			Snippet sn = snippets.remove(resourceId);
			if (sn != null) {
				byId.remove(sn.getIdentifier());
			}
		}
	}

	@Override
	public void clearSnippets() {
		synchronized (snippets) {
			snippets.clear();
			byId.clear();
		}
	}

	@Override
	public Optional<Snippet> getSnippet(String identifier) {
		synchronized (snippets) {
			return Optional.ofNullable(byId.get(identifier));
		}
	}

	@Override
	public Optional<Snippet> getResourceSnippet(String resourceId) {
		synchronized (snippets) {
			return Optional.ofNullable(snippets.get(resourceId));
		}
	}

}
