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

import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.function.generic.pages.EntitiesPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.PropertyEntitiesPage;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Generic property function wizard
 * 
 * @author Simon Templer
 */
public class GenericPropertyFunctionWizard extends
		AbstractGenericFunctionWizard<PropertyParameter, PropertyFunction> {

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
	 * @see AbstractGenericFunctionWizard#getFunction()
	 */
	@Override
	public PropertyFunction getFunction() {
		PropertyFunctionExtension pfe = PropertyFunctionExtension.getInstance();
		return pfe.get(getFunctionId());
	}

	/**
	 * @see AbstractGenericFunctionWizard#createEntitiesPage(SchemaSelection,
	 *      Cell)
	 */
	@Override
	protected EntitiesPage<PropertyFunction, PropertyParameter, ?> createEntitiesPage(
			SchemaSelection initSelection, Cell initCell) {
		return new PropertyEntitiesPage(initSelection, initCell);
	}

}
