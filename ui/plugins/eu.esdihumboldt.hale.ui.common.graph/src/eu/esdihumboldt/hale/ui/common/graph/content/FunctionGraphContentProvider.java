/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
