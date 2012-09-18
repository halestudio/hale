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

package eu.esdihumboldt.hale.ui.common.graph.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.zest.core.viewers.INestedContentProvider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Graph entity relationship content provider that models entities and cells as
 * nodes. Property entites are contained in type entities. Supports an
 * {@link Alignment}, a {@link Cell} or an {@link Iterable} of {@link Cell}s as
 * input.
 * 
 * @author Simon Templer
 */
public class NestedCellRelationshipContentProvider extends CellRelationshipContentProvider
		implements INestedContentProvider {

	private Multimap<Type, Property> entityMap = HashMultimap.create();

	/**
	 * @see CellRelationshipContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		List<Object> elements = new ArrayList<Object>();

		entityMap.clear();

		Multimap<TypeDefinition, Type> types = HashMultimap.create();
		Collection<Property> properties = new ArrayList<Property>();

		for (Object element : super.getElements(input)) {
			if (element instanceof Type) {
				Type type = (Type) element;
				types.put(type.getDefinition().getDefinition(), type);
				elements.add(element);
			}
			else if (element instanceof Property) {
				properties.add((Property) element);
			}
			else {
				elements.add(element);
			}
		}

		// assign properties to corresponding parents
		for (Property property : properties) {
			// find association through type definition
			TypeDefinition parentType = property.getDefinition().getType();
			Collection<Type> typeList = types.get(parentType);
			for (Type type : typeList) {
				entityMap.put(type, property);
			}
		}

		return elements.toArray();
	}

	/**
	 * @see INestedContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Type) {
			return true;
		}
//		if (element instanceof Cell) {
//			return AlignmentUtil.isTypeCell((Cell) element);
//		}
		return false;
	}

	/**
	 * @see INestedContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof Type) {
			return entityMap.get((Type) element).toArray();
		}

		return null;
	}

}
