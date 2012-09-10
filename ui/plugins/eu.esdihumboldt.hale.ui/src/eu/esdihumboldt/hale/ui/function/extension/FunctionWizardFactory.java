/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.function.extension;

import eu.esdihumboldt.hale.common.align.model.Cell;
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
	 * Creates a wizard for editing an existing cell.
	 * 
	 * @param cell the cell to edit
	 * @return the new wizard instance
	 */
	public FunctionWizard createEditWizard(Cell cell);

}
