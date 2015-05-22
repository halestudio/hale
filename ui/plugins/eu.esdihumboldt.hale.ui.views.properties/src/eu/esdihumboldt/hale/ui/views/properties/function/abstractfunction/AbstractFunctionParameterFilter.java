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

package eu.esdihumboldt.hale.ui.views.properties.function.abstractfunction;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;

/**
 * Filter that lets only {@link FunctionDefinition}s with defined parameters
 * that are not empty pass.
 * 
 * @author Patrick Lieb
 */
public class AbstractFunctionParameterFilter implements IFilter {

	/**
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	@Override
	public boolean select(Object toTest) {
		if (toTest instanceof FunctionDefinition) {
			return !((FunctionDefinition<?>) toTest).getDefinedParameters().isEmpty();
		}
		return false;
	}

}
