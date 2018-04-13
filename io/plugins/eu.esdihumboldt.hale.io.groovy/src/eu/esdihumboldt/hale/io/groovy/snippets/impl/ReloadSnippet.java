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

import java.io.File;
import java.nio.charset.Charset;

import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.io.groovy.snippets.Snippet;
import groovy.lang.Script;

/**
 * Snippet that is reloaded when the underlying file has changed.
 * 
 * @author Simon Templer
 */
public class ReloadSnippet implements Snippet {

	private final File snippetFile;
	private final String id;
	private final Charset encoding;

	private final long lastMod = -1;
	private Script lastScript = null;

	/**
	 * Create a snippet.
	 * 
	 * @param snippetFile the snippet file
	 * @param id the snippet identifier
	 * @param encoding the encoding of the snippet file
	 */
	public ReloadSnippet(File snippetFile, String id, Charset encoding) {
		this.snippetFile = snippetFile;
		this.id = id;
		this.encoding = encoding;
	}

	@Override
	public Script getScript(ServiceProvider services) throws Exception {
		long mod = snippetFile.lastModified();

		if (mod > lastMod) {
			lastScript = SnippetReaderImpl.loadSnippet(new FileIOSupplier(snippetFile), services,
					encoding);
		}

		return lastScript;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

}
