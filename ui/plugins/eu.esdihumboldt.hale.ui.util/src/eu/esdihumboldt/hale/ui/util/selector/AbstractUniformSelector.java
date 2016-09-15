/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.util.selector;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

/**
 * Abstract selector control based on a {@link TableViewer}.
 * 
 * @param <T> the type of the object to be selected
 * @author Simon Templer
 */
public abstract class AbstractUniformSelector<T> extends AbstractSelector<T, T> {

	/**
	 * @see AbstractSelector#AbstractSelector(Composite, ILabelProvider,
	 *      ViewerFilter[])
	 */
	public AbstractUniformSelector(Composite parent, ILabelProvider labelProvider,
			ViewerFilter[] filters) {
		super(parent, labelProvider, filters);
	}

	@Override
	protected T convertFrom(T object) {
		return object;
	}

}
