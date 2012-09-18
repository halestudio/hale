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

package eu.esdihumboldt.hale.ui.views.properties.definition;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * The default filter for all filters
 * 
 * @author Patrick Lieb
 */
public abstract class DefaultDefinitionFilter implements IFilter {

	/**
	 * Determine if an input is invalid and thus should be rejected by the
	 * filter.
	 * 
	 * @param input the definition
	 * @return <code>true</code> if the definition should be rejected by the
	 *         filter, <code>false</code> otherwise
	 */
	public abstract boolean isFiltered(Definition<?> input);

	/**
	 * @see IFilter#select(Object)
	 */
	@Override
	public boolean select(Object input) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof Entity) {
			input = ((Entity) input).getDefinition();
		}

		if (input instanceof EntityDefinition) {
			input = ((EntityDefinition) input).getDefinition();
		}

		if (input instanceof Definition<?>) {
			return !isFiltered((Definition<?>) input);
		}

		return false;
	}
}
