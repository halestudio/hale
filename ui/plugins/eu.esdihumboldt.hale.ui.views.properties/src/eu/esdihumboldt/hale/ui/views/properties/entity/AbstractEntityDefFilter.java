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

package eu.esdihumboldt.hale.ui.views.properties.entity;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;

/**
 * Filter base class for {@link EntityDefinition} filters.
 * 
 * @author Simon Templer
 */
public abstract class AbstractEntityDefFilter implements IFilter {

	/**
	 * Determine if an entity definition passes the filter.
	 * 
	 * @param input the entity definition
	 * @return <code>true</code> if the entity definition is accepted,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean accept(EntityDefinition input);

	/**
	 * @see IFilter#select(Object)
	 */
	@Override
	public boolean select(Object input) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof EntityDefinition) {
			return accept((EntityDefinition) input);
		}

		return false;
	}
}
