/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.service.align.resolver.internal;

import org.eclipse.jface.viewers.Viewer

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import groovy.transform.CompileStatic

/**
 * Content provider that takes an iterable of {@link EntityDefinition}s as input
 * and displays them completely with their children.
 * 
 * @author Simon Templer
 */
@CompileStatic
class EntityContentProvider extends ArrayTreeContentProvider {

	private final Map<EntityDefinition, EntityDefinition> parentChild = new IdentityHashMap<>()

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput)

		parentChild.clear()

		// populate parent-child map
		newInput.each { EntityDefinition entity ->

			EntityDefinition child = entity
			EntityDefinition parent = AlignmentUtil.getParent(child)
			if (parent == null) {
				// type entity def
				// add a special entry with null child so it is recognized in #getElements
				parentChild[child] = (EntityDefinition) null
			}
			else {
				// property entity def
				while (parent != null) {
					parentChild[parent] = child
					child = parent
					parent = AlignmentUtil.getParent(child)
				}
			}
		}
	}

	@Override
	public void dispose() {
		parentChild.clear()

		super.dispose()
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// return all types listed as parents
		parentChild.keySet().findAll { EntityDefinition entity -> entity instanceof TypeEntityDefinition }.toArray()
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		EntityDefinition child = parentChild[(EntityDefinition) parentElement]
		if (child) {
			[child].toArray()
		}
		else {
			[].toArray()
		}
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
}
