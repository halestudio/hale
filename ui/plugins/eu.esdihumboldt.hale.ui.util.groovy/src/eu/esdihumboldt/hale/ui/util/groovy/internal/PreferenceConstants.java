/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.util.groovy.internal;

/**
 * Preference constants.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public interface PreferenceConstants {

	/**
	 * Preference key suffix for bold text style preference keys.
	 * 
	 * @since 2.1
	 */
	public static final String EDITOR_BOLD_SUFFIX = "_bold"; //$NON-NLS-1$

	/**
	 * Preference key suffix for italic text style preference keys.
	 * 
	 * @since 3.0
	 */
	public static final String EDITOR_ITALIC_SUFFIX = "_italic"; //$NON-NLS-1$

	/**
	 * Preference key suffix for strikethrough text style preference keys.
	 * 
	 * @since 3.1
	 */
	public static final String EDITOR_STRIKETHROUGH_SUFFIX = "_strikethrough"; //$NON-NLS-1$

	/**
	 * Preference key suffix for underline text style preference keys.
	 * 
	 * @since 3.1
	 */
	public static final String EDITOR_UNDERLINE_SUFFIX = "_underline"; //$NON-NLS-1$

	public static final String GROOVY_EDITOR_HIGHLIGHT_STRINGS_COLOR = "color_string";

	public static final String GROOVY_EDITOR_HIGHLIGHT_ANNOTATION_COLOR = "color_annotation";

	public static final String GROOVY_EDITOR_HIGHLIGHT_NUMBERS_COLOR = "color_numbers";

	public static final String GROOVY_EDITOR_DEFAULT_COLOR = "color_default";

	public static final String GROOVY_EDITOR_HIGHLIGHT_JAVAKEYWORDS_COLOR = "color_javakeywords";

	public static final String GROOVY_EDITOR_HIGHLIGHT_JAVATYPES_COLOR = "color_javatypes";

	public static final String GROOVY_EDITOR_HIGHLIGHT_GROOVYKEYWORDS_COLOR = "color_groovykeywords";

	public static final String GROOVY_EDITOR_HIGHLIGHT_GJDK_COLOR = "color_gjdk";

	public static final String GROOVY_EDITOR_HIGHLIGHT_BRACKET_COLOR = "color_bracket";

	public static final String GROOVY_EDITOR_HIGHLIGHT_OPERATOR_COLOR = "color_operator";

	public static final String GROOVY_EDITOR_HIGHLIGHT_RETURN_COLOR = "color_return";

	public static final String GROOVY_EDITOR_HIGHLIGHT_SLASHY_STRINGS = "highlight_slashy_strings";

	public static final String GROOVY_EDITOR_HIGHLIGHT_CUSTOM_KEYWORD_COLOR = "color_customkeyword";
}
