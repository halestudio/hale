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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil;

/**
 * Abstract type matcher. Finds candidates matching a custom parameter.
 * 
 * @param <T> the match parameter type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractTypeMatcher<T> {

	/**
	 * Path candidate
	 */
	private static class PathCandidate {

		private final TypeDefinition type;
		private final DefinitionPath path;
		private final HashSet<TypeDefinition> checkedTypes;

		/**
		 * Constructor
		 * 
		 * @param type the associated type
		 * @param path the definition path
		 * @param checkedTypes the type definitions that have already been
		 *            checked (to prevent cycles)
		 */
		public PathCandidate(TypeDefinition type, DefinitionPath path,
				HashSet<TypeDefinition> checkedTypes) {
			this.type = type;
			this.path = path;
			this.checkedTypes = checkedTypes;
		}

		/**
		 * @return the attributeType
		 */
		public TypeDefinition getType() {
			return type;
		}

		/**
		 * @return the definitionPath
		 */
		public DefinitionPath getPath() {
			return path;
		}

		/**
		 * @return the handledTypes
		 */
		public HashSet<TypeDefinition> getCheckedTypes() {
			return checkedTypes;
		}

	}

	/**
	 * Find candidates for a possible path
	 * 
	 * @param elementType the start element type
	 * @param elementName the start element name
	 * @param unique if the start element cannot be repeated
	 * @param matchParam the match parameter
	 * 
	 * @return the path candidates
	 */
	public List<DefinitionPath> findCandidates(TypeDefinition elementType, QName elementName,
			boolean unique, T matchParam) {
		return findCandidates(elementType, elementName, unique, matchParam, Integer.MAX_VALUE);
	}

	/**
	 * Find candidates for a possible path
	 * 
	 * @param elementType the start element type
	 * @param elementName the start element name
	 * @param unique if the start element cannot be repeated
	 * @param matchParam the match parameter
	 * @param maxDepth the maximum depth that is allowed for paths
	 * 
	 * @return the path candidates
	 */
	public List<DefinitionPath> findCandidates(TypeDefinition elementType, QName elementName,
			boolean unique, T matchParam, int maxDepth) {
		Queue<PathCandidate> candidates = new LinkedList<PathCandidate>();
		PathCandidate base = new PathCandidate(elementType, new DefinitionPath(elementType,
				elementName, unique), new HashSet<TypeDefinition>());
		candidates.add(base);

		while (!candidates.isEmpty()) {
			PathCandidate candidate = candidates.poll();
			TypeDefinition type = candidate.getType();
			DefinitionPath basePath = candidate.getPath();
			HashSet<TypeDefinition> checkedTypes = candidate.getCheckedTypes();

			if (basePath.getSteps().size() >= maxDepth) {
				// max depth already reached, only direct match allowed

				// check if there is a direct match
				DefinitionPath path = matchPath(type, matchParam, basePath);
				if (path != null) {
					return Collections.singletonList(path); // return instantly
				}
				// skip sub paths
				continue;
			}

			if (checkedTypes.contains(type)) {
				continue; // prevent cycles
			}
			else {
				checkedTypes.add(type);
			}

			// check if there is a direct match
			DefinitionPath path = matchPath(type, matchParam, basePath);
			if (path != null) {
				return Collections.singletonList(path); // return instantly
				// XXX currently always only one path is returned - this might
				// change if we allow matchPath to yield multiple results
			}

			if (!type.getConstraint(AbstractFlag.class).isEnabled()) {
				// only allow stepping down properties if the type is not
				// abstract

				// step down properties
				// XXX why differentiate here?
				@SuppressWarnings("unchecked")
				Iterable<ChildDefinition<?>> children = (Iterable<ChildDefinition<?>>) ((basePath
						.isEmpty() || basePath.getLastElement().isProperty()) ? (type.getChildren())
						: (type.getDeclaredChildren()));
				Iterable<DefinitionPath> childPaths = GmlWriterUtil.collectPropertyPaths(children,
						basePath, true);
				for (DefinitionPath childPath : childPaths) {
					// only descend into elements
					candidates.add(new PathCandidate(childPath.getLastType(), childPath,
							new HashSet<TypeDefinition>(checkedTypes)));
				}
			}

			// step down sub-types
			// XXX done through choice
//			Set<TypeDefinition> substitutionTypes = new HashSet<TypeDefinition>();
//			for (SchemaElement element : type.getSubstitutions(basePath.getLastName())) {
//				substitutionTypes.add(element.getType());
//				candidates.add(new PathCandidate(element.getType(),
//						new DefinitionPath(basePath).addSubstitution(element),
//						new HashSet<TypeDefinition>(checkedTypes)));
//			}

			// step down sub-types - elements may be downcast using xsi:type
			if (!type.getConstraint(AbstractFlag.class).isEnabled()) {
				/*
				 * don't do it for abstract types as they have no element that
				 * may be used XXX is this true?
				 */

				for (@SuppressWarnings("unused")
				TypeDefinition subtype : type.getSubTypes()) {
					// FIXME how to determine which types are ok for xsi:type?!
//					if (!substitutionTypes.contains(subtype)) { // only types that are no valid substitutions
//						// add candidate
////						Name element = basePath.getLastName(); // the element name that will be extended with xsi:type
//						candidates.add(new PathCandidate(subtype,
//								new DefinitionPath(basePath).addDowncast(subtype),
//								new HashSet<TypeDefinition>(checkedTypes)));
//					}
				}
			}
		}

		return new ArrayList<DefinitionPath>();
	}

	/**
	 * Determines if a type definition is compatible with the match parameter
	 * 
	 * @param type the type definition
	 * @param matchParam the match parameter
	 * @param path the current definition path
	 * 
	 * @return the (eventually updated) definition path if a match is found,
	 *         otherwise <code>null</code>
	 */
	protected abstract DefinitionPath matchPath(TypeDefinition type, T matchParam,
			DefinitionPath path);

}
