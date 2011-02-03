/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.writers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.gmlwriter.impl.internal.GmlWriterUtil;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.PathElement;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Represents a pattern for matching an abstract path
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Pattern {

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
		
		private final Name name;

		/**
		 * Constructor
		 * 
		 * @param type the element type
		 * @param name the element name
		 */
		public PatternElement(ElementType type, Name name) {
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
		public Name getName() {
			return name;
		}
		
	}
	
	private static final String ELEMENT_DELIMITER = "/";
	private static final String WILDCARD_ONE = "*";
	private static final String WILDCARD_ANY = "**";
	private static final String NS_MARKER = "\"";
	
	/**
	 * Parse a pattern from the given string. Pattern elements must be separated
	 * by <code>/</code>. Valid elements are <code>*</code> (one XML element 
	 * with any name), <code>**</code> (any number of XML elements with any 
	 * name) and an XML element name. An XML element name may also include a
	 * namespace, the namespace must be wrapped by quotes (<code>"</code>).
	 * If no namespace is specified the GML namespace is assumed.
	 * 
	 * @param pattern the pattern string
	 * 
	 * @return the parsed pattern
	 */
	public static Pattern parse(String pattern) {
		List<PatternElement> elements = new ArrayList<PatternElement>();
		
		String[] parts = pattern.split(ELEMENT_DELIMITER);
		
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
							throw new IllegalArgumentException("No terminating namespace quote found");
						}
						else {
							// separate namespace and name
							int index = part.lastIndexOf(NS_MARKER);
							String namespace = part.substring(1, index).trim();
							String name = part.substring(index + 1).trim();
							
							elements.add(new PatternElement(
									ElementType.NAMED_ELEMENT, 
									new NameImpl(namespace, name)));
						}
					}
					else {
						// element name only
						elements.add(new PatternElement(
								ElementType.NAMED_ELEMENT, 
								new NameImpl(part)));
					}
				}
			}
		}
		
		return new Pattern(pattern, elements);
	}

	private final List<PatternElement> elements;
	
	private final String patternString;
	
	/**
	 * Constructor
	 * 
	 * @param patternString the pattern string 
	 * @param elements the pattern elements
	 */
	private Pattern(String patternString, List<PatternElement> elements) {
		super();
		
		this.patternString = patternString;
		this.elements = elements;
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
	public DefinitionPath match(TypeDefinition type, DefinitionPath path,
			String gmlNs) {
		return match(type, path, gmlNs, new HashSet<TypeDefinition>(),
				new LinkedList<PatternElement>(elements));
	}
	
	/**
	 * Matches the type against the encoding pattern.
	 * 
	 * @param type the type definition
	 * @param path the definition path
	 * @param gmlNs the GML namespace
	 * @param checkedTypes the type definitions that have already been checked
	 *   (to prevent cycles)
	 * @param remainingElements the remeining elements to match
	 * 
	 * @return the new path if there is a match, <code>null</code> otherwise
	 */
	private static DefinitionPath match(TypeDefinition type, DefinitionPath path,
			String gmlNs, HashSet<TypeDefinition> checkedTypes,
			List<PatternElement> remainingElements) {
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
			removeFirstForAttributeDescent = true; // first element may not be removed for sub-type descent
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
			throw new IllegalStateException("Unknown pattern element type");
		}
		
		if (checkAgainst != null) {
			// get the last path element
			Name elementName;
			PathElement pe = path.getLastElement();
			if (pe != null) {
				elementName = pe.getName();
			}
			else {
				// path is empty -> use type element name
				elementName = GmlWriterUtil.getElementName(type);
			}
			
			Name name = checkAgainst.getName();
			// inject namespace if needed
			if (name.getNamespaceURI() == null) {
				name = new NameImpl(gmlNs, name.getLocalPart());
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
			}
			else {
				// no name match
				// sub-type descent is still allowed, don't remove element
			}
		}
		
		// descend further
		
		if (allowSubtypeDescent) {
			// step down sub-types
			for (TypeDefinition subtype : type.getSubTypes()) {
				DefinitionPath candidate = match(
						subtype, 
						new DefinitionPath(path).addSubType(subtype), 
						gmlNs, 
						new HashSet<TypeDefinition>(checkedTypes), 
						new ArrayList<PatternElement>(remainingElements));
				
				if (candidate != null) {
					return candidate;
				}
			}
		}
		
		if (allowAttributeDescent) {
			if (removeFirstForAttributeDescent) {
				remainingElements.remove(0);
			}
			
			// step down properties
			Iterable<AttributeDefinition> properties = (path.isEmpty())?(type.getAttributes()):(type.getDeclaredAttributes());
			for (AttributeDefinition att : properties) {
				DefinitionPath candidate = match(
						att.getAttributeType(),
						new DefinitionPath(path).addProperty(att),
						gmlNs,
						new HashSet<TypeDefinition>(checkedTypes),
						new ArrayList<PatternElement>(remainingElements));
				
				if (candidate != null) {
					return candidate;
				}
			}
		}
		
		return null;
	}

	/**
	 * Determines if the pattern is valid. To be valid it must at least contain
	 * one named element
	 * 
	 * @return if the pattern is valid
	 */
	public boolean isValid() {
		for (PatternElement element : elements) {
			if (element.getType().equals(ElementType.NAMED_ELEMENT)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return patternString;
	}
	
}
