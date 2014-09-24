/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;

/**
 * Represents a pattern for matching an abstract path
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class Pattern {

	/**
	 * Pattern type
	 */
	private enum PatternType {
		/** combines multiple patterns with an AND relation */
		AND,
		/** combines multiple patterns with an OR relation */
		OR,
		/** matches a pattern */
		MATCH
	}

	/**
	 * Valid pattern element types
	 */
	private static enum ElementType {
		/**
		 * Represents one XML element with any name
		 */
		ONE_ELEMENT,
		/**
		 * Represents any number of XML elements with any names
		 */
		ANY_ELEMENTS,
		/**
		 * Represents an XML element with a certain name
		 */
		NAMED_ELEMENT
	}

	/**
	 * A pattern element
	 */
	private static class PatternElement {

		private final ElementType type;

		private final QName name;

		/**
		 * Constructor
		 * 
		 * @param type the element type
		 * @param name the element name
		 */
		public PatternElement(ElementType type, QName name) {
			super();
			this.type = type;
			this.name = name;
		}

		/**
		 * @return the type
		 */
		public ElementType getType() {
			return type;
		}

		/**
		 * @return the name
		 */
		public QName getName() {
			return name;
		}

	}

	private static final String ELEMENT_DELIMITER = "/"; //$NON-NLS-1$
	private static final String WILDCARD_ONE = "*"; //$NON-NLS-1$
	private static final String WILDCARD_ANY = "**"; //$NON-NLS-1$
	private static final String NS_MARKER = "\""; //$NON-NLS-1$

	/**
	 * Placeholder for the GML namespace that may be used in patterns
	 */
	public static final String GML_NAMESPACE_PLACEHOLDER = "_____________gml_____________";

	/**
	 * Parse a pattern from the given string. Pattern elements must be separated
	 * by <code>/</code>. Valid elements are <code>*</code> (one XML element
	 * with any name), <code>**</code> (any number of XML elements with any
	 * name) and an XML element name. An XML element name may also include a
	 * namespace, the namespace must be wrapped by quotes (<code>"</code>). If
	 * no namespace is specified the GML namespace is assumed.
	 * 
	 * @param pattern the pattern string
	 * 
	 * @return the parsed pattern
	 */
	public static Pattern parse(String pattern) {
		List<PatternElement> elements = new ArrayList<PatternElement>();

		String[] parts = pattern.split(ELEMENT_DELIMITER);

		// TODO - * or ** after ** not allowed

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];

			if (part != null && !part.trim().isEmpty()) {
				part = part.trim();

				if (part.equals(WILDCARD_ONE)) {
					// one element
					elements.add(new PatternElement(ElementType.ONE_ELEMENT, null));
				}
				else if (part.equals(WILDCARD_ANY)) {
					// any elements
					elements.add(new PatternElement(ElementType.ANY_ELEMENTS, null));
				}
				else {
					// name or namespace + name
					if (part.startsWith(NS_MARKER)) {
						// namespace + name

						// check if the part contains another marker
						boolean isLast = part.length() > 1 && part.substring(1).contains(NS_MARKER);

						// add parts until name is complete
						while (!isLast && i < parts.length - 1) {
							String next = parts[++i];

							isLast = next.contains(NS_MARKER);

							part += next;
						}

						if (!isLast) {
							throw new IllegalArgumentException(
									"No terminating namespace quote found"); //$NON-NLS-1$
						}
						else {
							// separate namespace and name
							int index = part.lastIndexOf(NS_MARKER);
							String namespace = part.substring(1, index).trim();
							String name = part.substring(index + 1).trim();

							elements.add(new PatternElement(ElementType.NAMED_ELEMENT, new QName(
									namespace, name)));
						}
					}
					else {
						// element name only, assuming GML namespace
						elements.add(new PatternElement(ElementType.NAMED_ELEMENT, new QName(
								GML_NAMESPACE_PLACEHOLDER, part)));
					}
				}
			}
		}

		return new Pattern(PatternType.MATCH, pattern, elements, null);
	}

	/**
	 * Create a pattern that combines multiple patterns with a logical OR. When
	 * matching, the path of the first pattern that matches is returned.
	 * 
	 * @param patterns the sub-patterns
	 * 
	 * @return the OR pattern
	 */
	public static Pattern or(Pattern... patterns) {
		return new Pattern(PatternType.OR, null, null, Arrays.asList(patterns));
	}

	/**
	 * Create a pattern that combines multiple patterns with a logical AND. When
	 * matching, all patterns must match and the path of the first pattern is
	 * returned.
	 * 
	 * @param patterns the sub-patterns
	 * 
	 * @return the AND pattern
	 */
	public static Pattern and(Pattern... patterns) {
		return new Pattern(PatternType.AND, null, null, Arrays.asList(patterns));
	}

	private final List<PatternElement> elements;

	private final List<Pattern> subPatterns;

	private final String patternString;

	private final PatternType type;

	/**
	 * Constructor
	 * 
	 * @param type the pattern type
	 * @param patternString the pattern string
	 * @param elements the pattern elements
	 * @param subPatterns the sub-patterns
	 */
	private Pattern(PatternType type, String patternString, List<PatternElement> elements,
			List<Pattern> subPatterns) {
		super();

		this.patternString = patternString;
		this.elements = elements;
		this.subPatterns = subPatterns;
		this.type = type;
	}

	/**
	 * Matches the type against the encoding pattern.
	 * 
	 * @param type the type definition
	 * @param path the definition path
	 * @param gmlNs the GML namespace
	 * 
	 * @return the new path if there is a match, <code>null</code> otherwise
	 */
	public DefinitionPath match(TypeDefinition type, DefinitionPath path, String gmlNs) {
		switch (this.type) {
		case AND: {
			DefinitionPath fPath = null;
			for (Pattern pattern : subPatterns) {
				DefinitionPath res = pattern.match(type, path, gmlNs);
				if (res == null) {
					// all must match
					return null;
				}
				else if (fPath == null) {
					// remember the first path
					fPath = res;
				}
			}
			return fPath;
		}
		case OR: {
			for (Pattern pattern : subPatterns) {
				DefinitionPath res = pattern.match(type, path, gmlNs);
				if (res != null) {
					// any must match
					return res;
				}
			}
			return null; // none matched
		}
		case MATCH:
		default:
			return match(type, path, gmlNs, new HashSet<TypeDefinition>(),
					new LinkedList<PatternElement>(elements));
		}
	}

	/**
	 * Matches the type against the encoding pattern.
	 * 
	 * @param type the type definition
	 * @param path the definition path
	 * @param gmlNs the GML namespace
	 * @param checkedTypes the type definitions that have already been checked
	 *            (to prevent cycles)
	 * @param remainingElements the remaining elements to match
	 * 
	 * @return the new path if there is a match, <code>null</code> otherwise
	 */
	private static DefinitionPath match(TypeDefinition type, DefinitionPath path, String gmlNs,
			HashSet<TypeDefinition> checkedTypes, List<PatternElement> remainingElements) {
		if (remainingElements == null || remainingElements.isEmpty()) {
			return null;
		}

		if (checkedTypes.contains(type)) {
			return null;
		}
		else {
			checkedTypes.add(type);
		}

		PatternElement first = remainingElements.get(0);
		PatternElement checkAgainst;
		boolean allowAttributeDescent;
		boolean removeFirstForAttributeDescent = false;
		boolean allowSubtypeDescent = true;
		switch (first.getType()) {
		case ONE_ELEMENT:
			checkAgainst = null; // only descend
			allowAttributeDescent = true;
			removeFirstForAttributeDescent = true; // first element may not be
													// removed for sub-type
													// descent
			// special case: was last element
			if (remainingElements.size() == 1) {
				return path;
			}
			break;
		case ANY_ELEMENTS:
			// check against the next named element
			PatternElement named = null;
			for (int i = 1; i < remainingElements.size() && named == null; i++) {
				PatternElement element = remainingElements.get(i);
				if (element.getType().equals(ElementType.NAMED_ELEMENT)) {
					named = element;
				}
			}
			if (named == null) {
				// no named element
				return null;
			}
			else {
				checkAgainst = named;
			}
			allowAttributeDescent = true;
			break;
		case NAMED_ELEMENT:
			checkAgainst = first; // check the current
			allowAttributeDescent = false; // only allow sub-type descent
			break;
		default:
			throw new IllegalStateException("Unknown pattern element type"); //$NON-NLS-1$
		}

		if (checkAgainst != null) {
			// get the last path element
			QName elementName = path.getLastName();

			QName name = checkAgainst.getName();
			// inject namespace if needed
			if (name.getNamespaceURI() == GML_NAMESPACE_PLACEHOLDER) {
				name = new QName(gmlNs, name.getLocalPart());
			}

			// check direct match
			if (name.equals(elementName)) {
				// match for the element name -> we are on the right track

				int index = remainingElements.indexOf(checkAgainst);
				if (index == remainingElements.size() - 1) {
					// is last - we have a full match
					return path;
				}

				// remove the element (and any leading wildcards) from the queue
				remainingElements = remainingElements.subList(index + 1, remainingElements.size());
				// for a name match, no sub-type descent is allowed
				allowSubtypeDescent = false;
				// but an attribute descent is ok
				allowAttributeDescent = true;
			}
			else {
				// no name match
				// sub-type descent is still allowed, don't remove element
			}
		}

		// descend further

		if (allowSubtypeDescent) {
			// step down sub-types
			// XXX now represented in choices
			// XXX sub-type must work through parent choice
//			for (SchemaElement element : type.getSubstitutions(path.getLastName())) {
//				DefinitionPath candidate = match(
//						element.getType(), 
//						new DefinitionPath(path).addSubstitution(element), 
//						gmlNs, 
//						new HashSet<TypeDefinition>(checkedTypes), 
//						new ArrayList<PatternElement>(remainingElements));
//				
//				if (candidate != null) {
//					return candidate;
//				}
//			}
		}

		if (allowAttributeDescent) {
			if (removeFirstForAttributeDescent) {
				remainingElements.remove(0);
			}

			// step down properties
			@java.lang.SuppressWarnings("unchecked")
			Iterable<ChildDefinition<?>> children = (Iterable<ChildDefinition<?>>) ((path.isEmpty()) ? (type
					.getChildren()) : (type.getDeclaredChildren()));
			Iterable<DefinitionPath> childPaths = GmlWriterUtil.collectPropertyPaths(children,
					path, true);
			for (DefinitionPath childPath : childPaths) {
				DefinitionPath candidate = match(childPath.getLastType(), childPath, gmlNs,
						new HashSet<TypeDefinition>(checkedTypes), new ArrayList<PatternElement>(
								remainingElements));

				if (candidate != null) {
					return candidate;
				}
			}
		}

		return null;
	}

	/**
	 * Determines if the pattern is valid.
	 * 
	 * @return if the pattern is valid
	 */
	public boolean isValid() {
//		for (PatternElement element : elements) {
//			if (element.getType().equals(ElementType.NAMED_ELEMENT)) {
//				return true;
//			}
//		}
//		
//		return false;
		// XXX for now assume any pattern is valid
		return true;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		switch (type) {
		case AND:
			return relationString(" AND "); //$NON-NLS-1$
		case OR:
			return relationString(" OR "); //$NON-NLS-1$
		case MATCH:
		default:
			return patternString;
		}
	}

	private String relationString(String delimiter) {
		if (subPatterns == null)
			throw new IllegalStateException("Sub-patterns must be set for AND/OR patterns"); //$NON-NLS-1$

		StringBuffer result = new StringBuffer("("); //$NON-NLS-1$
		boolean first = true;
		for (Pattern pattern : subPatterns) {
			if (first) {
				first = false;
			}
			else {
				result.append(delimiter);
			}

			result.append(pattern.toString());
		}
		result.append(")"); //$NON-NLS-1$

		return result.toString();
	}

}
