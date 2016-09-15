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

package eu.esdihumboldt.hale.ui.views.properties.function.propertyfunction;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;

/**
 * Filter that lets only {@link PropertyFunctionDefinition}s with a source or a
 * target that is not empty pass.
 * 
 * @author Patrick Lieb
 */
public class PropertyFunctionGraphFilter implements IFilter {

	/**
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	@Override
	public boolean select(Object toTest) {
		if (toTest instanceof PropertyFunctionDefinition) {
			return !(((PropertyFunctionDefinition) toTest).getSource().isEmpty() && ((PropertyFunctionDefinition) toTest)
					.getTarget().isEmpty());
		}
		return false;
	}
}
