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

import java.net.URI;
import java.nio.charset.Charset;

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.io.groovy.snippets.Snippet;
import groovy.lang.Script;

/**
 * Snippet that is already loaded.
 * 
 * @author Simon Templer
 */
public class URISnippet implements Snippet {

	private Script lastScript = null;
	private final String id;
	private final URI location;
	private final Charset encoding;

	/**
	 * Create a new snippet.
	 * 
	 * @param location the snippet location
	 * @param id the snippet identifier
	 * @param encoding the snippet encoding
	 */
	public URISnippet(URI location, String id, Charset encoding) {
		this.location = location;
		this.id = id;
		this.encoding = encoding;
	}

	@Override
	public void invalidate() {
		lastScript = null;
	}

	@Override
	public synchronized Script getScript(ServiceProvider services) throws Exception {
		if (lastScript == null) {
			lastScript = SnippetReaderImpl.loadSnippet(new DefaultInputSupplier(location), services,
					encoding);
		}

		return lastScript;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

}
