/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.wizards.functions.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractExtension;
import eu.esdihumboldt.hale.ui.wizards.functions.extension.impl.PropertyFunctionWizardDescriptorImpl;
import eu.esdihumboldt.hale.ui.wizards.functions.extension.impl.TypeFunctionWizardDescriptorImpl;

/**
 * {@link FunctionWizardFactory} extension
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FunctionWizardExtension extends AbstractExtension<FunctionWizardFactory, FunctionWizardDescriptor<?>> {
	
	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.wizards.functions"; //$NON-NLS-1$
	
	private static FunctionWizardExtension instance;
	
	/**
	 * Get the extension instance
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

}
