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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameterDefinition;
import eu.esdihumboldt.util.Pair;

/**
 * Graph content provider to model the source and target of a
 * {@link FunctionDefinition}
 * 
 * @author Patrick Lieb
 */
public class FunctionGraphContentProvider extends ArrayContentProvider
		implements IGraphEntityContentProvider {

	/**
	 * @see IGraphEntityContentProvider#getConnectedTo(Object)
	 */
	@Override
	public Object[] getConnectedTo(Object entity) {
		Collection<Object> result = new ArrayList<Object>();
		if (entity instanceof FunctionDefinition) {
			return ((FunctionDefinition<?>) entity).getTarget().toArray();
		}
		if (entity instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) entity;
			if (pair.getFirst() instanceof AbstractParameter) {
				result.add(pair.getSecond());
				return result.toArray();
			}
		}
		return null;
	}

	/**
	 * @see ArrayContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		Collection<Object> collection = new ArrayList<Object>();
		if (inputElement instanceof FunctionDefinition<?>) {
			FunctionDefinition<?> function = (FunctionDefinition<?>) inputElement;
			collection.add(function);

			if (inputElement instanceof TypeFunctionDefinition) {
				for (TypeParameterDefinition type : ((TypeFunctionDefinition) function)
						.getSource()) {
					collection.add(new Pair<Object, Object>(type, function));
				}
				for (TypeParameterDefinition type : ((TypeFunctionDefinition) function)
						.getTarget()) {
					collection.add(type);
				}
			}

			if (inputElement instanceof PropertyFunctionDefinition) {
				for (PropertyParameterDefinition prop : ((PropertyFunctionDefinition) function)
						.getSource()) {
					collection.add(new Pair<Object, Object>(prop, function));
				}
				for (PropertyParameterDefinition prop : ((PropertyFunctionDefinition) function)
						.getTarget()) {
					collection.add(prop);
				}
			}
			return collection.toArray();

		}
		return super.getElements(inputElement);
	}

}
