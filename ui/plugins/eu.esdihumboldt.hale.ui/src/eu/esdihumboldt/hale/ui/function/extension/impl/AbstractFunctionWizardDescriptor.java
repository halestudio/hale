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

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractObjectFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;

/**
 * Abstract function wizard descriptor based on a configuration element
 * 
 * @param <T> the function definition type
 * @author Simon Templer
 */
public abstract class AbstractFunctionWizardDescriptor<T extends FunctionDefinition<?>> extends
		AbstractConfigurationFactory<FunctionWizardFactory> implements FunctionWizardDescriptor<T> {

	private FunctionWizardFactory factory;

	/**
	 * Create a function wizard descriptor based on the given configuration
	 * element
	 * 
	 * @param conf the configuration element
	 */
	protected AbstractFunctionWizardDescriptor(IConfigurationElement conf) {
		super(conf, "class");
	}

	/**
	 * @see ExtensionObjectFactory#dispose(Object)
	 */
	@Override
	public void dispose(FunctionWizardFactory instance) {
		// do nothing
	}

	/**
	 * @see ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * @see ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getFunction().getDisplayName();
	}

	/**
	 * @see FunctionWizardDescriptor#getFunctionId()
	 */
	@Override
	public String getFunctionId() {
		return conf.getAttribute("function");
	}

	/**
	 * @see AbstractObjectFactory#getIconURL()
	 */
	@Override
	public URL getIconURL() {
		return getFunction().getIconURL();
	}

	/**
	 * @see AbstractConfigurationFactory#createExtensionObject()
	 */
	@Override
	public FunctionWizardFactory createExtensionObject() throws Exception {
		String clazz = conf.getAttribute("class");
		try {
			if (clazz != null && !clazz.isEmpty()) {
				return super.createExtensionObject();
			}
		} catch (Exception e) {
			// TODO log message
		}

		return createDefaultFactory();
	}

	/**
	 * Create the default function wizard factory for the function if none is
	 * explicitly defined or its creation fails.
	 * 
	 * @return the default function wizard factory
	 */
	protected abstract FunctionWizardFactory createDefaultFactory();

	/**
	 * Get the associated function wizard factory
	 * 
	 * @return the function wizard factory
	 */
	protected FunctionWizardFactory getFactory() {
		if (factory == null) {
			try {
				factory = createExtensionObject();
			} catch (Exception e) {
				throw new IllegalStateException("Error creating function wizard factory");
			}
		}
		return factory;
	}

	/**
	 * @see FunctionWizardFactory#createNewWizard(SchemaSelection)
	 */
	@Override
	public FunctionWizard createNewWizard(SchemaSelection schemaSelection) {
		return getFactory().createNewWizard(schemaSelection);
	}

	/**
	 * @see FunctionWizardFactory#createEditWizard(Cell)
	 */
	@Override
	public FunctionWizard createEditWizard(Cell cell) {
		return getFactory().createEditWizard(cell);
	}

}
