/*
 /*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.common.align.groovy.accessor.internal;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.align.groovy.accessor.PathElement;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.groovy.paths.Path;

/**
 * Entity definition access utilites.
 * 
 * @author Simon Templer
 */
public class EntityAccessorUtil {

	/**
	 * Create an entity definition from a path.
	 * 
	 * @param path the path, the topmost element has to represent an
	 *            {@link EntityDefinition}, all other elements must represent
	 *            {@link ChildContext}s
	 * @return the created entity definition or <code>null</code> if the path
	 *         was <code>null</code>
	 */
	public static EntityDefinition createEntity(Path<PathElement> path) {
		if (path == null) {
			return null;
		}

		List<PathElement> elements = new ArrayList<>(path.getElements());

		// create entity definition
		PathElement top = elements.remove(0);
		if (top.getRoot() == null) {
			throw new IllegalArgumentException("Topmost path element must be an entity definition");
		}
		EntityDefinition entity = top.getRoot();

		// collect type information
		TypeDefinition type = entity.getType();
		SchemaSpaceID schemaSpace = entity.getSchemaSpace();
		Filter filter = entity.getFilter();

		List<ChildContext> contextPath = new ArrayList<>(entity.getPropertyPath());

		for (PathElement element : elements) {
			ChildContext cc = element.getChild();
			if (cc == null) {
				throw new IllegalArgumentException(
						"All child elements must be defined by a child context");
			}
			contextPath.add(cc);
		}
		return AlignmentUtil.createEntity(type, contextPath, schemaSpace, filter);
	}

}
