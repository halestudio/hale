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

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.extension.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.function.extension.TypeFunctionWizardDescriptor;
import eu.esdihumboldt.hale.ui.function.generic.GenericTypeFunctionWizardFactory;

/**
 * Type function wizard descriptor
 * 
 * @author Simon Templer
 */
public class TypeFunctionWizardDescriptorImpl extends
		AbstractFunctionWizardDescriptor<TypeFunctionDefinition> implements
		TypeFunctionWizardDescriptor {

	/**
	 * @see AbstractFunctionWizardDescriptor#AbstractFunctionWizardDescriptor(IConfigurationElement)
	 */
	public TypeFunctionWizardDescriptorImpl(IConfigurationElement conf) {
		super(conf);
	}

	/**
	 * @see FunctionWizardDescriptor#getFunction()
	 */
	@Override
	public TypeFunctionDefinition getFunction() {
		return FunctionUtil.getTypeFunction(getFunctionId(), HaleUI.getServiceProvider());
	}

	/**
	 * @see AbstractFunctionWizardDescriptor#createDefaultFactory()
	 */
	@Override
	protected FunctionWizardFactory createDefaultFactory() {
		return new GenericTypeFunctionWizardFactory(getFunctionId());
	}

}
