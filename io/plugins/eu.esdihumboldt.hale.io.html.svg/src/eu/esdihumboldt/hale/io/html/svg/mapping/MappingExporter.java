/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.html.svg.mapping;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.common.io.CharStreams;

import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;

/**
 * Exports an alignment as HTML mapping documentation based on
 * {@link MappingDocumentation} and the associated template.
 * 
 * @author Simon Templer
 */
public class MappingExporter extends AbstractAlignmentWriter {

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Generate mapping documentation", ProgressIndicator.UNKNOWN);

		// retrieve template URL
		URL templateUrl = getClass().getResource("mapping.html");

		// create template binding
		@SuppressWarnings("unchecked")
		Map<String, Object> binding = MappingDocumentation.createBinding(getProjectInfo(),
				getAlignment());

		// read javascript from file and store it in the binding
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(
				"render-mapping.js"), StandardCharsets.UTF_8)) {
			binding.put("javascript", CharStreams.toString(reader));
		}

		// initialize template engine
		GStringTemplateEngine engine = new GStringTemplateEngine();

		// bind and write template
		try (Writer out = new OutputStreamWriter(getTarget().getOutput(), StandardCharsets.UTF_8)) {
			Writable template = engine.createTemplate(templateUrl).make(binding);
			template.writeTo(out);

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Error creating mapping documentation", e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}

		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "HTML Documentation";
	}
}
