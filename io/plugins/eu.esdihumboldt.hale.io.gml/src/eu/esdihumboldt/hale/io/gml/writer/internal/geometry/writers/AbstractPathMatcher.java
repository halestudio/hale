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

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;

/**
 * Abstract pattern based path matcher
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractPathMatcher {

	private static final ALogger log = ALoggerFactory.getLogger(AbstractPathMatcher.class);

	private final Set<Pattern> basePatterns = new HashSet<Pattern>();

	private final Set<Pattern> verifyPatterns = new HashSet<Pattern>();

	/**
	 * Add a base pattern. When matching the path the pattern path is appended
	 * to the base path.
	 * 
	 * @param pattern the pattern string
	 * @see Pattern#parse(String)
	 */
	public void addBasePattern(String pattern) {
		Pattern p = Pattern.parse(pattern);
		if (p.isValid()) {
			basePatterns.add(p);
		}
		else {
			log.warn("Ignoring invalid pattern: " + pattern); //$NON-NLS-1$
		}
	}

	/**
	 * Add a verification pattern. If a match for a base pattern is found the
	 * verification patterns will be used to verify the structure. For a path to
	 * be accepted, all verification patterns must match and the resulting
	 * end-points of the verification patterns must be valid.
	 * 
	 * @see #verifyEndPoint(TypeDefinition)
	 * 
	 * @param pattern the pattern string
	 * @see Pattern#parse(String)
	 */
	public void addVerificationPattern(String pattern) {
		Pattern p = Pattern.parse(pattern);
		if (p.isValid()) {
			verifyPatterns.add(p);
		}
		else {
			log.warn("Ignoring invalid pattern: " + pattern); //$NON-NLS-1$
		}
	}

	/**
	 * Add a verification pattern. If a match for a base pattern is found the
	 * verification patterns will be used to verify the structure. For a path to
	 * be accepted, all verification patterns must match and the resulting
	 * end-points of the verification patterns must be valid.
	 * 
	 * @see #verifyEndPoint(TypeDefinition)
	 * 
	 * @param pattern the pattern
	 * @see Pattern#parse(String)
	 */
	public void addVerificationPattern(Pattern pattern) {
		if (pattern.isValid()) {
			verifyPatterns.add(pattern);
		}
		else {
			log.warn("Ignoring invalid pattern: " + pattern); //$NON-NLS-1$
		}
	}

	/**
	 * Matches the type against the encoding patterns.
	 * 
	 * @param type the type definition
	 * @param basePath the definition path
	 * @param defaultNs the default namespace for the patterns
	 * 
	 * @return the new path if there is a match, <code>null</code> otherwise
	 */
	public DefinitionPath match(TypeDefinition type, DefinitionPath basePath, String defaultNs) {
		// try to match each base pattern
		for (Pattern pattern : basePatterns) {
			DefinitionPath path = pattern.match(type, basePath, defaultNs);
			if (path != null) {
				// verification patterns
				if (verifyPatterns != null && !verifyPatterns.isEmpty()) {
					for (Pattern verPattern : verifyPatterns) {
						DefinitionPath endPoint = verPattern.match(path.getLastType(),
								new DefinitionPath(path), defaultNs);
						if (endPoint != null) {
							// verify end-point
							boolean ok = verifyEndPoint(endPoint.getLastType());
							if (!ok) {
								// all end-points must be valid
								return null;
							}
						}
						else {
							// all verification patterns must match
							return null;
						}
					}
				}
				else {
					// no verify patterns -> check base pattern end-point
					boolean ok = verifyEndPoint(path.getLastType());
					if (!ok) {
						return null;
					}
				}

				/*
				 * now either all verification patterns matched and the
				 * end-points were valid, or no verification patterns were
				 * specified and the base pattern end-point was valid
				 */
				return path;
			}
		}

		return null;
	}

	/**
	 * Descend the given pattern
	 * 
	 * @param writer the XML stream writer
	 * @param descendPattern the pattern to descend
	 * @param elementType the type of the encompassing element
	 * @param elementName the encompassing element name
	 * @param defaultNs the pattern default namespace
	 * @param unique if the path's start element cannot be repeated
	 * @return the descent that was opened, it must be closed to close the
	 *         opened elements
	 * @throws XMLStreamException if an error occurs writing the coordinates
	 */
	public static Descent descend(XMLStreamWriter writer, Pattern descendPattern,
			TypeDefinition elementType, QName elementName, String defaultNs, boolean unique)
			throws XMLStreamException {
		DefinitionPath path = descendPattern.match(elementType, new DefinitionPath(elementType,
				elementName, unique), defaultNs);

		return Descent.descend(writer, path, null, true);
	}

	/**
	 * Verify the verification end point. After reaching the end-point of a
	 * verification pattern this method is called with the
	 * {@link TypeDefinition} of the end-point to assure the needed structure is
	 * present (e.g. a DirectPositionListType element). If no verification
	 * pattern is present the end-point of the matched base pattern will be
	 * verified.
	 * 
	 * @param endPoint the end-point type definition
	 * 
	 * @return if the end-point is valid
	 */
	protected abstract boolean verifyEndPoint(TypeDefinition endPoint);

}
