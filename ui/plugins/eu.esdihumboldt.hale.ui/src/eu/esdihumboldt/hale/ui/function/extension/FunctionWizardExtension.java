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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.extension.impl.FactoryWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.impl.PropertyFunctionWizardDescriptorImpl;
import eu.esdihumboldt.hale.ui.function.extension.impl.TypeFunctionWizardDescriptorImpl;
import eu.esdihumboldt.hale.ui.function.generic.GenericPropertyFunctionWizardFactory;
import eu.esdihumboldt.hale.ui.function.generic.GenericTypeFunctionWizardFactory;

/**
 * {@link FunctionWizardFactory} extension
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FunctionWizardExtension extends
		AbstractExtension<FunctionWizardFactory, FunctionWizardDescriptor<?>> {

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.function"; //$NON-NLS-1$

	private static FunctionWizardExtension instance;

	/**
	 * Get the extension instance
	 * 
	 * @return the function wizard extension
	 */
	public static FunctionWizardExtension getInstance() {
		if (instance == null) {
			instance = new FunctionWizardExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private FunctionWizardExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected FunctionWizardDescriptor<?> createFactory(IConfigurationElement conf)
			throws Exception {
		// typeWizard
		if (conf.getName().equals("typeWizard")) { //$NON-NLS-1$
			return new TypeFunctionWizardDescriptorImpl(conf);
		}
		// propertyWizard
		else if (conf.getName().equals("propertyWizard")) { //$NON-NLS-1$
			return new PropertyFunctionWizardDescriptorImpl(conf);
		}

		return null;
	}

	/**
	 * Get the wizard descriptor for the given function ID. If no wizard
	 * descriptor is available for the function in the extension, a generic
	 * wizard descriptor will be created.
	 * 
	 * @param functionId the function ID
	 * @return the wizard descriptor
	 */
	public FunctionWizardDescriptor<?> getWizardDescriptor(final String functionId) {
		// retrieve matching wizards from extension
		List<FunctionWizardDescriptor<?>> factories = getFactories(new FactoryFilter<FunctionWizardFactory, FunctionWizardDescriptor<?>>() {

			@Override
			public boolean acceptFactory(FunctionWizardDescriptor<?> factory) {
				return factory.getFunctionId().equals(functionId);
			}

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<FunctionWizardFactory, FunctionWizardDescriptor<?>> collection) {
				return true;
			}
		});

		if (factories != null && !factories.isEmpty()) {
			return factories.get(0);
		}

		// try to create descriptor for generic wizard

		// check if type function
		TypeFunctionDefinition typeFunction = FunctionUtil.getTypeFunction(functionId,
				HaleUI.getServiceProvider());
		if (typeFunction != null) {
			return new FactoryWizardDescriptor<TypeFunctionDefinition>(
					new GenericTypeFunctionWizardFactory(functionId), typeFunction);
		}

		// check if property function
		PropertyFunctionDefinition propertyFunction = FunctionUtil.getPropertyFunction(functionId,
				HaleUI.getServiceProvider());
		if (propertyFunction != null) {
			return new FactoryWizardDescriptor<PropertyFunctionDefinition>(
					new GenericPropertyFunctionWizardFactory(functionId), propertyFunction);
		}

		throw new IllegalArgumentException("Function with ID " + functionId + " is unknown");
	}

}
