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
package eu.esdihumboldt.hale.ui.function.extension;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Factory for function wizards
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface FunctionWizardFactory {

	/**
	 * Creates a wizard for creating a new cell based on the given schema
	 * selection.
	 * 
	 * @param schemaSelection the schema selection or <code>null</code> if no
	 *            pre-selection is available
	 * @return the new wizard instance
	 */
	public FunctionWizard createNewWizard(SchemaSelection schemaSelection);

	/**
	 * Creates a wizard for creating a new cell based on the given schema
	 * selection.
	 * 
	 * @param schemaSelection the schema selection or <code>null</code> if no
	 *            pre-selection is available
	 * @param parameters initial function parameters
	 * @return the new wizard instance
	 */
	public FunctionWizard createNewWizard(SchemaSelection schemaSelection,
			ListMultimap<String, ParameterValue> parameters);

	/**
	 * Creates a wizard for editing an existing cell.
	 * 
	 * @param cell the cell to edit
	 * @return the new wizard instance
	 */
	public FunctionWizard createEditWizard(Cell cell);

}
