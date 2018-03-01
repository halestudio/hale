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

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.generic.pages.EntitiesPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.PropertyEntitiesPage;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Generic property function wizard
 * 
 * @author Simon Templer
 */
public class GenericPropertyFunctionWizard extends
		AbstractGenericFunctionWizard<PropertyParameterDefinition, PropertyFunctionDefinition> {

	/**
	 * @see AbstractGenericFunctionWizard#AbstractGenericFunctionWizard(Cell)
	 */
	public GenericPropertyFunctionWizard(Cell cell) {
		super(cell);
	}

	/**
	 * @see AbstractGenericFunctionWizard#AbstractGenericFunctionWizard(SchemaSelection,
	 *      String)
	 */
	public GenericPropertyFunctionWizard(SchemaSelection selection, String functionId) {
		super(selection, functionId);
	}

	/**
	 * @see AbstractGenericFunctionWizard#AbstractGenericFunctionWizard(SchemaSelection,
	 *      ListMultimap, String)
	 */
	public GenericPropertyFunctionWizard(SchemaSelection selection,
			ListMultimap<String, ParameterValue> parameters, String functionId) {
		super(selection, parameters, functionId);
	}

	/**
	 * @see AbstractGenericFunctionWizard#getFunction()
	 */
	@Override
	public PropertyFunctionDefinition getFunction() {
		return FunctionUtil.getPropertyFunction(getFunctionId(), HaleUI.getServiceProvider());
	}

	/**
	 * @see AbstractGenericFunctionWizard#createEntitiesPage(SchemaSelection,
	 *      Cell)
	 */
	@Override
	protected EntitiesPage<PropertyFunctionDefinition, PropertyParameterDefinition, ?> createEntitiesPage(
			SchemaSelection initSelection, Cell initCell) {
		return new PropertyEntitiesPage(initSelection, initCell);
	}

}
