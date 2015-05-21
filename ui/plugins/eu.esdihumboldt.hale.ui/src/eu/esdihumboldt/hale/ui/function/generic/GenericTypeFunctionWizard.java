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

package eu.esdihumboldt.hale.ui.function.generic;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.generic.pages.EntitiesPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.TypeEntitiesPage;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Generic type function wizard
 * 
 * @author Simon Templer
 */
public class GenericTypeFunctionWizard extends
		AbstractGenericFunctionWizard<TypeParameter, TypeFunction> {

	/**
	 * @see AbstractGenericFunctionWizard#AbstractGenericFunctionWizard(Cell)
	 */
	public GenericTypeFunctionWizard(Cell cell) {
		super(cell);
	}

	/**
	 * @see AbstractGenericFunctionWizard#AbstractGenericFunctionWizard(SchemaSelection,
	 *      String)
	 */
	public GenericTypeFunctionWizard(SchemaSelection selection, String functionId) {
		super(selection, functionId);
	}

	/**
	 * @see AbstractGenericFunctionWizard#getFunction()
	 */
	@Override
	public TypeFunction getFunction() {
		return FunctionUtil.getTypeFunction(getFunctionId(), HaleUI.getServiceProvider());
	}

	/**
	 * @see AbstractGenericFunctionWizard#createEntitiesPage(SchemaSelection,
	 *      Cell)
	 */
	@Override
	protected EntitiesPage<TypeFunction, TypeParameter, ?> createEntitiesPage(
			SchemaSelection initSelection, Cell initCell) {
		return new TypeEntitiesPage(initSelection, initCell);
	}

}
