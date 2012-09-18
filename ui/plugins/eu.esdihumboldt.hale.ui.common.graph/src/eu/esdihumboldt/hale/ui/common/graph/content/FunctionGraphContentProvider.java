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

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameter;
import eu.esdihumboldt.util.Pair;

/**
 * Graph content provider to model the source and target of a {@link Function}
 * 
 * @author Patrick Lieb
 */
public class FunctionGraphContentProvider extends ArrayContentProvider implements
		IGraphEntityContentProvider {

	/**
	 * @see IGraphEntityContentProvider#getConnectedTo(Object)
	 */
	@Override
	public Object[] getConnectedTo(Object entity) {
		Collection<Object> result = new ArrayList<Object>();
		if (entity instanceof Function) {
			return ((Function) entity).getTarget().toArray();
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
		if (inputElement instanceof AbstractFunction<?>) {
			AbstractFunction<?> function = (AbstractFunction<?>) inputElement;
			collection.add(function);

			if (inputElement instanceof TypeFunction) {
				for (TypeParameter type : ((TypeFunction) function).getSource()) {
					collection.add(new Pair<Object, Object>(type, function));
				}
				for (TypeParameter type : ((TypeFunction) function).getTarget()) {
					collection.add(type);
				}
			}

			if (inputElement instanceof PropertyFunction) {
				for (PropertyParameter prop : ((PropertyFunction) function).getSource()) {
					collection.add(new Pair<Object, Object>(prop, function));
				}
				for (PropertyParameter prop : ((PropertyFunction) function).getTarget()) {
					collection.add(prop);
				}
			}
			return collection.toArray();

		}
		return super.getElements(inputElement);
	}

}
