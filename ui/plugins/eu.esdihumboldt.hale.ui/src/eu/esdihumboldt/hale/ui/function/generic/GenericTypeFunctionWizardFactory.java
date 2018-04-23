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

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Factory for generic type function wizards
 * 
 * @author Simon Templer
 */
public class GenericTypeFunctionWizardFactory extends AbstractGenericFunctionWizardFactory {

	/**
	 * @see AbstractGenericFunctionWizardFactory#AbstractGenericFunctionWizardFactory(String)
	 */
	public GenericTypeFunctionWizardFactory(String functionId) {
		super(functionId);
	}

	/**
	 * @see FunctionWizardFactory#createNewWizard(SchemaSelection)
	 */
	@Override
	public FunctionWizard createNewWizard(SchemaSelection schemaSelection) {
		return new GenericTypeFunctionWizard(schemaSelection, getFunctionId());
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory#createNewWizard(eu.esdihumboldt.hale.ui.selection.SchemaSelection,
	 *      com.google.common.collect.ListMultimap)
	 */
	@Override
	public FunctionWizard createNewWizard(SchemaSelection schemaSelection,
			ListMultimap<String, ParameterValue> parameters) {
		return new GenericTypeFunctionWizard(schemaSelection, parameters, getFunctionId());
	}

	/**
	 * @see FunctionWizardFactory#createEditWizard(Cell)
	 */
	@Override
	public FunctionWizard createEditWizard(Cell cell) {
		assert getFunctionId().equals(cell.getTransformationIdentifier());
		return new GenericTypeFunctionWizard(cell);
	}
}
