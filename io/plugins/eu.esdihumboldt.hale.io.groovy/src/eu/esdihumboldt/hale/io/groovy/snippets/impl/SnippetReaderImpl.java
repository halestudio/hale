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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Default snippet reader.
 * 
 * @author Simon Templer
 */
public class SnippetReaderImpl extends AbstractSnippetReader {

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		String id = getIdentifier();

		if (id == null || id.isEmpty()) {
			throw new IOProviderConfigurationException("Identifier for the snippet must be set");
		}

		progress.begin("Load Groovy snippet", ProgressIndicator.UNKNOWN);
		try {
			URI loc = getSource().getLocation();
			File snippetFile = null;
			if (loc != null && isAutoReload()) {
				try {
					snippetFile = new File(loc);
				} catch (Exception e) {
					// ignore
				}
			}

			if (snippetFile != null) {
				// lazy load snippet
				ReloadSnippet rl = new ReloadSnippet(snippetFile, id, getCharset());

				// trigger first loading of script
				try {
					rl.getScript(getServiceProvider());
				} catch (Exception e) {
					reporter.error("Attempt to load script failed", e);
				}

				setSnippet(rl);
			}
			else {
				Script script = loadSnippet(getSource(), getServiceProvider(), getCharset());
				setSnippet(new LoadedSnippet(script, id));
			}

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error("Loading snippet failed", e);
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;
	}

	/**
	 * Load a snippet script.
	 * 
	 * @param source the source of the script
	 * @param serviceProvider the service provider
	 * @param encoding the encoding
	 * @return the loaded script
	 * @throws Exception if loading or parsing the script fails
	 */
	public static Script loadSnippet(LocatableInputSupplier<? extends InputStream> source,
			ServiceProvider serviceProvider, Charset encoding) throws Exception {
		GroovyService service = serviceProvider.getService(GroovyService.class);
		Binding binding = null;

		String script;
		try (InputStream in = source.getInput()) {
			script = IOUtils.toString(in, encoding);
		}

		Script result = service.parseScript(script, binding);
		return result;
	}

}
