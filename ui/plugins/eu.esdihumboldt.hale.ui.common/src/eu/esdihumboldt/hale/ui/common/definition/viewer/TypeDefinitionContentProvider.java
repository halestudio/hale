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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Content provider for a tree representing the structure of
 * {@link TypeDefinition}s. Requires a {@link Collection} of
 * {@link TypeDefinition} as input.
 * 
 * @author Simon Templer
 */
public class TypeDefinitionContentProvider extends TypeIndexContentProvider {

	/**
	 * @see TypeIndexContentProvider#TypeIndexContentProvider(TreeViewer)
	 */
	public TypeDefinitionContentProvider(TreeViewer tree) {
		super(tree);
	}

	/**
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Collection<?>)
			return ((Collection<?>) inputElement).toArray();
		else
			throw new IllegalArgumentException(
					"Content provider only applicable for a collection of type definitions.");
	}

}
