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

package eu.esdihumboldt.hale.ui.function.extension.impl;

import java.net.URL;

import de.cs3d.util.eclipse.extension.AbstractObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Function wizard descriptor based on a {@link FunctionWizardFactory} instance.
 * @param <T> the function type
 * @author Simon Templer
 */
public class FactoryWizardDescriptor<T extends AbstractFunction<?>> extends AbstractObjectFactory<FunctionWizardFactory> implements FunctionWizardDescriptor<T> {

	private static final String IDENTIFIER_PREFIX = "GENERIC_";
	
	private final FunctionWizardFactory factory;
	
	private final T function;
	
	/**
	 * Create a function wizard descriptor based on the given factory.
	 * @param factory the function wizard factory
	 * @param function the associated function
	 */
	public FactoryWizardDescriptor(FunctionWizardFactory factory, T function) {
		super();
		this.factory = factory;
		this.function = function;
	}

	@Override
	public FunctionWizardFactory createExtensionObject() throws Exception {
		return factory;
	}

	@Override
	public void dispose(FunctionWizardFactory instance) {
		// do nothing
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER_PREFIX + function.getId();
	}

	@Override
	public String getDisplayName() {
		return function.getDisplayName();
	}

	@Override
	public String getTypeName() {
		return factory.getClass().getName();
	}

	@Override
	public FunctionWizard createNewWizard(SchemaSelection schemaSelection) {
		return factory.createNewWizard(schemaSelection);
	}

	@Override
	public FunctionWizard createEditWizard(Cell cell) {
		return factory.createEditWizard(cell);
	}

	@Override
	public String getFunctionId() {
		return function.getId();
	}

	@Override
	public T getFunction() {
		return function;
	}

	@Override
	public URL getIconURL() {
		return function.getIconURL();
	}

}
