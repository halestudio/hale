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

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaEntityTypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.util.Pair;

/**
 * Subclass of the tree content provider
 * {@link SchemaEntityTypeIndexContentProvider}, which can handle metadatas of
 * instances.
 * 
 * @author Sebastian Reinhardt
 */
public class TypeMetaPairContentProvider extends SchemaEntityTypeIndexContentProvider {

	/**
	 * @see SchemaEntityTypeIndexContentProvider#SchemaEntityTypeIndexContentProvider(TreeViewer,
	 *      SchemaSpaceID)
	 */
	public TypeMetaPairContentProvider(TreeViewer tree, SchemaSpaceID schemaSpace) {
		super(tree, schemaSpace);
	}

	/**
	 * @see TypeIndexContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) inputElement;
			Object type = pair.getFirst();
			if (type instanceof TypeDefinition) {
				type = new TypeEntityDefinition((TypeDefinition) type, getSchemaSpace(), null);
			}
			// second item will be a set of metadata keys
			return new Object[] { type, pair.getSecond() };
		}
		else
			return new Object[0];
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Set<?>) {
			return ((Set<String>) parentElement).toArray();
		}
		else
			return super.getChildren(parentElement);
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof Set<?>) {
			return !((Set<String>) parentElement).isEmpty();
		}
		if (parentElement instanceof String) {
			return false;
		}
		else
			return super.hasChildren(parentElement);
	}

}
