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

package eu.esdihumboldt.hale.ui.function.extension.impl;

import java.net.URL;

import com.google.common.collect.ListMultimap;

import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Function wizard descriptor based on a {@link FunctionWizardFactory} instance.
 * 
 * @param <T> the function type
 * @author Simon Templer
 */
public class FactoryWizardDescriptor<T extends FunctionDefinition<?>> extends
		AbstractObjectFactory<FunctionWizardFactory> implements FunctionWizardDescriptor<T> {

	private static final String IDENTIFIER_PREFIX = "GENERIC_";

	private final FunctionWizardFactory factory;

	private final T function;

	/**
	 * Create a function wizard descriptor based on the given factory.
	 * 
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
	public FunctionWizard createNewWizard(SchemaSelection schemaSelection,
			ListMultimap<String, ParameterValue> parameters) {
		return factory.createNewWizard(schemaSelection, parameters);
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
