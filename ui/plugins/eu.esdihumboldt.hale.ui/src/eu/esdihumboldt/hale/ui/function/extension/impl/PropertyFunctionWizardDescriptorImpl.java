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
