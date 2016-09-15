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
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.html.svg.mapping.json.AlignmentJson;
import eu.esdihumboldt.hale.io.html.svg.mapping.json.CellJsonExtension;
import eu.esdihumboldt.hale.io.html.svg.mapping.json.ExtendedCellRepresentation;
import eu.esdihumboldt.hale.io.html.svg.mapping.json.JsonValueRepresentation;
import eu.esdihumboldt.hale.io.html.svg.mapping.json.ValueRepresentation;
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder;
import groovy.json.JsonOutput;
import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;

/**
 * Exports an alignment as HTML mapping documentation based on
 * {@link AlignmentJson} and the associated template.
 * 
 * @author Simon Templer
 */
public class MappingExporter extends AbstractAlignmentWriter {

	private static final String HALEJS_VERSION = "1.2.0-SNAPSHOT";

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

		// generate Json representation
		CellJsonExtension ext = new ExtendedCellRepresentation(getAlignment(),
				getServiceProvider());
		ValueRepresentation rep = new JsonValueRepresentation();

		StringWriter jsonWriter = new StringWriter();
		JsonStreamBuilder json = new JsonStreamBuilder(jsonWriter, true);
		Set<Locale> locales = AlignmentJson.alignmentInfoJSON(getAlignment(), json,
				getServiceProvider(), getProjectInfo(), ext, rep, Locale.getDefault(),
				getSourceSchema(), getTargetSchema());

		// create language binding
		String languageJson = getLanguageJson(locales);

		// create template binding
		Map<String, Object> binding = new HashMap<>();
		binding.put("json", jsonWriter.toString());
		String title = (getProjectInfo() != null && getProjectInfo().getName() != null)
				? getProjectInfo().getName() : "Mapping documentation";
		binding.put("title", title);
		binding.put("languages", languageJson);
		binding.put("halejsVersion", HALEJS_VERSION);

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

	private String getLanguageJson(Set<Locale> locales) {
		if (locales == null || locales.isEmpty()) {
			return "[]";
		}

		Map<String, String> languageNames = new HashMap<>();

		for (Locale locale : locales) {
			String code = locale.getLanguage();
			if (code != null && !code.isEmpty()) {
				languageNames.put(code, locale.getDisplayLanguage(locale));
			}
		}

		List<Map<String, String>> locs = languageNames.entrySet().stream().map(entry -> {
			Map<String, String> languageObj = new HashMap<>();
			languageObj.put("code", entry.getKey());
			languageObj.put("name", entry.getValue());
			return languageObj;
		}).collect(Collectors.toList());

		return JsonOutput.toJson(locs);
	}

	@Override
	protected String getDefaultTypeName() {
		return "HTML Documentation";
	}
}
