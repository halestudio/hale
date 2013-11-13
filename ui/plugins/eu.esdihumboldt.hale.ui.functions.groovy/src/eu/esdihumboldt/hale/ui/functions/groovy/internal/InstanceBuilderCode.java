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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import eu.esdihumboldt.hale.common.schema.Classification;
import eu.esdihumboldt.hale.common.schema.groovy.DefinitionAccessor;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Instance builder code generation utils.
 * 
 * @author Simon Templer
 */
public class InstanceBuilderCode {

	/**
	 * Append example code to build the properties identified by the given path
	 * tree.
	 * 
	 * @param example the example code to append to
	 * @param indentCount the indent count to use
	 * @param tree the path tree representing a specific segment
	 * @param parent the parent of the segment
	 * @param useBrackets if brackets should be used in the generated code
	 */
	public static void appendBuildProperties(StringBuilder example, int indentCount, PathTree tree,
			DefinitionGroup parent, final boolean useBrackets) {
		appendBuildProperties(example, createIndent(indentCount), tree, parent, useBrackets, true,
				true, true);
	}

	/**
	 * Append example code to build the properties identified by the given path
	 * tree.
	 * 
	 * @param example the example code to append to
	 * @param baseIndent the base indent to use
	 * @param tree the path tree representing a specific segment
	 * @param parent the parent of the segment
	 * @param useBrackets if brackets should be used in the generated code
	 * @param startWithIndent if at the beginning the indent should be added
	 * @param endWithNewline if at the end a new line break should be added
	 * @param useExampleValues if example values should be used
	 * @return the relative offset where editing should be continued
	 */
	public static int appendBuildProperties(StringBuilder example, String baseIndent,
			PathTree tree, DefinitionGroup parent, final boolean useBrackets,
			final boolean startWithIndent, final boolean endWithNewline,
			final boolean useExampleValues) {
		Definition<?> def = (Definition<?>) tree.getSegment();
		final String indent = baseIndent;
		int cursor = 0;
		boolean opened = false;

		if (def instanceof PropertyDefinition) {
			// property name
			if (startWithIndent) {
				example.append(indent);
			}
			// TODO test if property must be accessed explicitly through
			// builder?
			example.append(def.getName().getLocalPart());

			// test if uniquely accessible from parent
			boolean useNamespace = true;
			if (parent instanceof Definition<?>) {
				try {
					new DefinitionAccessor((Definition<?>) parent).findChildren(
							def.getName().getLocalPart()).eval();
					useNamespace = false;
				} catch (IllegalStateException e) {
					// ignore - namespace needed
				}
			}

			boolean needComma = false;

			// add namespace if necessary
			if (useNamespace) {
				if (useBrackets && !needComma) {
					example.append('(');
				}
				example.append(" namespace: '");
				example.append(def.getName().getNamespaceURI());
				example.append('\'');
				needComma = true;
			}

			TypeDefinition propertyType = ((PropertyDefinition) def).getPropertyType();
			boolean hasValue = propertyType.getConstraint(HasValueFlag.class).isEnabled();
			if (hasValue) {
				// add an example value
				if (useBrackets && !needComma) {
					example.append('(');
				}
				if (needComma) {
					example.append(',');
				}
				example.append(' ');
				if (useExampleValues) {
					switch (Classification.getClassification(def)) {
					case NUMERIC_PROPERTY:
						example.append("42");
						break;
					case STRING_PROPERTY:
						example.append("'some value'");
						break;
					default:
						example.append("some_value");
					}
				}

				needComma = true;
			}

			if (DefinitionUtil.hasChildren(propertyType)
					&& (!tree.getChildren().isEmpty() || !needComma || !hasValue)) {
				if (needComma) {
					if (useBrackets) {
						example.append(" )");
					}
					else {
						example.append(',');
					}
				}
				example.append(" {");
				example.append('\n');
				opened = true;
			}
			else {
				cursor = example.length();
				if (useBrackets && needComma) {
					example.append(" )");
				}
				if (endWithNewline) {
					example.append('\n');
				}
			}
		}
		else {
			// groups are ignored
		}

		if (opened) {
			// set the new parent
			parent = DefinitionUtil.getDefinitionGroup(def);

			// create child properties
			String newIndent = indent + createIndent(1);
			if (tree.getChildren().isEmpty()) {
				example.append(newIndent);
				cursor = example.length();
				example.append('\n');
			}
			else {
				for (PathTree child : tree.getChildren()) {
					cursor = appendBuildProperties(example, newIndent, child, parent, useBrackets,
							true, true, useExampleValues);
				}
			}

			// close bracket
			example.append(indent);
			example.append('}');
			if (endWithNewline) {
				example.append('\n');
			}
		}

		return cursor;
	}

	/**
	 * Create an indent of tabs.
	 * 
	 * @param count the tab count
	 * @return the indent
	 */
	public static String createIndent(int count) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++) {
			builder.append('\t');
		}
		return builder.toString();
	}

}
