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

import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
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
		TypeFunctionExtension tfe = TypeFunctionExtension.getInstance();
		return tfe.get(getFunctionId());
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
