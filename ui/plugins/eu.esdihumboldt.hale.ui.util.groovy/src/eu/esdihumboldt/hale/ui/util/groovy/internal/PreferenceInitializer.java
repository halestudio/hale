/*
 * Copyright 2003-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.esdihumboldt.hale.ui.util.groovy.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import eu.esdihumboldt.hale.ui.util.groovy.ColorConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = GroovyUIPlugin.getDefault().getPreferenceStore();

//        store.setDefault(PreferenceConstants.GROOVY_LOG_TRACE_MESSAGES_ENABLED, false);

		// Syntax coloring
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_GJDK_COLOR, new RGB(102, 204, 255));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_JAVAKEYWORDS_COLOR, new RGB(151, 44,
						120));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_GROOVYKEYWORDS_COLOR, new RGB(151, 44,
						120));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_JAVATYPES_COLOR, new RGB(151, 44, 120));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_STRINGS_COLOR, new RGB(255, 0, 204));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_NUMBERS_COLOR, new RGB(205, 50, 0));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_ANNOTATION_COLOR, new RGB(70, 70, 70));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_RETURN_COLOR, new RGB(151, 44, 120));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_BRACKET_COLOR, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_OPERATOR_COLOR, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(store, PreferenceConstants.GROOVY_EDITOR_DEFAULT_COLOR,
				new RGB(0, 0, 0));
		PreferenceConverter.setDefault(store,
				PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_CUSTOM_KEYWORD_COLOR, new RGB(0, 0, 0));

		PreferenceConverter.setDefault(store, ColorConstants.JAVA_MULTI_LINE_COMMENT, //
				new RGB(63, 127, 95));
		PreferenceConverter.setDefault(store, ColorConstants.JAVA_SINGLE_LINE_COMMENT, //
				new RGB(63, 127, 95));
		PreferenceConverter.setDefault(store, ColorConstants.JAVA_STRING, new RGB(255, 0, 204));
		PreferenceConverter.setDefault(store, ColorConstants.JAVADOC_TAG, new RGB(127, 159, 191));
		PreferenceConverter.setDefault(store, ColorConstants.JAVADOC_DEFAULT, new RGB(63, 95, 191));
		PreferenceConverter.setDefault(store, ColorConstants.JAVADOC_KEYWORD,
				new RGB(127, 127, 159));
		PreferenceConverter.setDefault(store, ColorConstants.JAVADOC_LINK, new RGB(63, 63, 191));

		// bold syntax
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_JAVAKEYWORDS_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_GROOVYKEYWORDS_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_JAVATYPES_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_RETURN_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_GJDK_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_STRINGS_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_NUMBERS_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_ANNOTATION_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_BRACKET_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_OPERATOR_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_DEFAULT_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_CUSTOM_KEYWORD_COLOR
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, true);

		// Dollar slashy string highlighting
		store.setDefault(PreferenceConstants.GROOVY_EDITOR_HIGHLIGHT_SLASHY_STRINGS, true);

		// JUnit Monospace font
//        store.setDefault(PreferenceConstants.GROOVY_JUNIT_MONOSPACE_FONT, false);

		// Ask to convert Legacy Projects at startup
//        store.setDefault(PreferenceConstants.GROOVY_ASK_TO_CONVERT_LEGACY_PROJECTS, true);

		// Semantic highlighting
//        store.setDefault(PreferenceConstants.GROOVY_SEMANTIC_HIGHLIGHTING, true);

		// Groovier Content assist
//        store.setDefault(PreferenceConstants.GROOVY_CONTENT_ASSIST_NOPARENS, true);
//        store.setDefault(PreferenceConstants.GROOVY_CONTENT_ASSIST_BRACKETS, true);
//        store.setDefault(PreferenceConstants.GROOVY_CONTENT_NAMED_ARGUMENTS, false);
//        store.setDefault(PreferenceConstants.GROOVY_CONTENT_PARAMETER_GUESSING, true);
//
//        store.setDefault(PreferenceConstants.GROOVY_SCRIPT_DEFAULT_WORKING_DIRECTORY, PreferenceConstants.GROOVY_SCRIPT_PROJECT_HOME);
	}

}
