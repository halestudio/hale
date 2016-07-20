/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.custom;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.impl.mdexpl.MarkdownCellExplanation;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import groovy.text.Template;

/**
 * Explanation for custom functions based on Markdown templates.
 * 
 * @author Simon Templer
 */
public class DefaultCustomFunctionExplanation extends MarkdownCellExplanation
		implements CellExplanation {

	private static final ALogger log = ALoggerFactory
			.getLogger(DefaultCustomFunctionExplanation.class);

	private final Map<Locale, Value> templates;

	private Supplier<FunctionDefinition<?>> functionResolver;

	/**
	 * Create an explanation with the given templates.
	 * 
	 * @param templates map of localized templates
	 * @param functionResolver the custom resolver for the associated function
	 *            definition, to be used instead of the usual mechanism to
	 *            resolve functions
	 */
	public DefaultCustomFunctionExplanation(Map<Locale, Value> templates,
			@Nullable Supplier<FunctionDefinition<?>> functionResolver) {
		super();
		this.templates = templates;
		this.functionResolver = functionResolver;
	}

	/**
	 * Create an explanation with the given templates.
	 * 
	 * @param templates map of localized templates
	 */
	public DefaultCustomFunctionExplanation(Map<Locale, Value> templates) {
		this(templates, null);
	}

	@Override
	public Iterable<Locale> getSupportedLocales() {
		return Collections.unmodifiableSet(templates.keySet());
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other the explanation to copy
	 */
	public DefaultCustomFunctionExplanation(DefaultCustomFunctionExplanation other) {
		super();
		if (other != null) {
			templates = new HashMap<>(other.templates);
			functionResolver = other.functionResolver;
		}
		else {
			templates = new HashMap<>();
			functionResolver = null;
		}
	}

	/**
	 * @param functionResolver the the custom resolver for the associated
	 *            function definition, to be used instead of the usual mechanism
	 *            to resolve functions
	 */
	public void setFunctionResolver(@Nullable Supplier<FunctionDefinition<?>> functionResolver) {
		this.functionResolver = functionResolver;
	}

	private Optional<Value> findTemplate(Locale locale) {
		ResourceBundle.Control control = ResourceBundle.Control
				.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);

		List<Locale> candidateLocales = control.getCandidateLocales("baseName", locale);

		for (Locale specificLocale : candidateLocales) {
			Value candidate = templates.get(specificLocale);
			if (candidate != null) {
				return Optional.of(candidate);
			}
		}

		return Optional.empty();
	}

	@Override
	protected Optional<Template> loadTemplate(Class<?> clazz, Locale locale) {
		return findTemplate(locale).flatMap(template -> {
			try {
				Text text = template.as(Text.class);
				if (text != null) {
					return Optional.ofNullable(getEngine().createTemplate(text.getText()));
				}
				else {
					String str = template.as(String.class);
					if (str != null) {
						return Optional.ofNullable(getEngine().createTemplate(str));
					}
					else {
						return Optional.empty();
					}
				}
			} catch (Exception e) {
				log.error("Could not load cell explanation template", e);
				return Optional.empty();
			}
		});
	}

	@Override
	protected FunctionDefinition<? extends ParameterDefinition> loadFunction(String functionId,
			ServiceProvider provider) {
		if (functionResolver != null) {
			return functionResolver.get();
		}
		return super.loadFunction(functionId, provider);
	}

	/**
	 * @return the configured explanation templates (not modifiable)
	 */
	public Map<Locale, Value> getTemplates() {
		return Collections.unmodifiableMap(templates);
	}

}
