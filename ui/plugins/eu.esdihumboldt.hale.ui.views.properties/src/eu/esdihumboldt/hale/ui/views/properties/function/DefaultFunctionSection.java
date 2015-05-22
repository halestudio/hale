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

package eu.esdihumboldt.hale.ui.views.properties.function;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.views.properties.AbstractTextSection;

/**
 * The default function section
 * 
 * @author Patrick Lieb
 * @param <F> the Function
 */
public class DefaultFunctionSection<F extends FunctionDefinition<?>> extends
		AbstractFunctionSection<F> {

	/**
	 * @see AbstractTextSection#setInput(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setInput(Object input) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof Cell) {
			String id = ((Cell) input).getTransformationIdentifier();
			input = FunctionUtil.getFunction(id, HaleUI.getServiceProvider());
		}

		if (input instanceof FunctionDefinition) {
			setFunction((F) input);
		}
	}

}
