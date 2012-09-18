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

import org.eclipse.core.runtime.IConfigurationElement;

import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.function.extension.PropertyFunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.generic.GenericPropertyFunctionWizardFactory;

/**
 * Property function wizard descriptor
 * 
 * @author Simon Templer
 */
public class PropertyFunctionWizardDescriptorImpl extends
		AbstractFunctionWizardDescriptor<PropertyFunction> implements
		PropertyFunctionWizardDescriptor {

	/**
	 * @see AbstractFunctionWizardDescriptor#AbstractFunctionWizardDescriptor(IConfigurationElement)
	 */
	public PropertyFunctionWizardDescriptorImpl(IConfigurationElement conf) {
		super(conf);
	}

	/**
	 * @see FunctionWizardDescriptor#getFunction()
	 */
	@Override
	public PropertyFunction getFunction() {
		PropertyFunctionExtension pfe = PropertyFunctionExtension.getInstance();
		return pfe.get(getFunctionId());
	}

	/**
	 * @see AbstractFunctionWizardDescriptor#createDefaultFactory()
	 */
	@Override
	protected FunctionWizardFactory createDefaultFactory() {
		return new GenericPropertyFunctionWizardFactory(getFunctionId());
	}

}
