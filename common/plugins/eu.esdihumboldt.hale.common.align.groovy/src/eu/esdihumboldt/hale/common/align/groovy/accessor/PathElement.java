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

package eu.esdihumboldt.hale.common.align.groovy.accessor;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Path element for {@link EntityAccessor}, which is either a type
 * entity definiton which must be the root of a path or a child context.
 * 
 * @author Simon Templer
 */
public class PathElement {

	private final EntityDefinition root;

	private final ChildContext child;

	/**
	 * Create a child path element.
	 * 
	 * @param child the child context
	 */
	public PathElement(ChildContext child) {
		super();
		this.child = child;
		this.root = null;
	}

	/**
	 * Create a root path element.
	 * 
	 * @param root the type entity definition
	 */
	public PathElement(EntityDefinition root) {
		super();
		this.root = root;
		this.child = null;
	}

	/**
	 * @return the root, may be <code>null</code>
	 */
	public EntityDefinition getRoot() {
		return root;
	}

	/**
	 * @return the child context, may be <code>null</code>
	 */
	public ChildContext getChild() {
		return child;
	}

	/**
	 * @return the definition represented by the path element
	 */
	public Definition<?> getDefinition() {
		if (root != null) {
			return root.getDefinition();
		}
		else {
			return child.getChild();
		}
	}

}
