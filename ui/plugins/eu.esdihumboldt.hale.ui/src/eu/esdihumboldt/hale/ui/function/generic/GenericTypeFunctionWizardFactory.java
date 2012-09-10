/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.function.generic;

import eu.esdihumboldt.hale.common.align.model.Cell;
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
	 * @see FunctionWizardFactory#createEditWizard(Cell)
	 */
	@Override
	public FunctionWizard createEditWizard(Cell cell) {
		assert getFunctionId().equals(cell.getTransformationIdentifier());
		return new GenericTypeFunctionWizard(cell);
	}

}
