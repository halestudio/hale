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
package eu.esdihumboldt.hale.rcp.wizards.functions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.wizards.functions.simple.SimpleFunctionWizardDescriptor;

/**
 * Utilities for the {@link FunctionWizardFactory} extension
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FunctionWizardExtension {
	
	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.wizards.functions.FunctionWizardFactory"; //$NON-NLS-1$
	
	/**
	 * Get the defined {@link FunctionWizardFactory}(ie)s
	 * 
	 * @return the defined function wizard factories
	 */
	public static List<FunctionWizardDescriptor> getFunctionWizards() {
		IConfigurationElement[] confArray = Platform.getExtensionRegistry().getConfigurationElementsFor(ID);
		
		List<FunctionWizardDescriptor> result = new ArrayList<FunctionWizardDescriptor>();
		
		for (IConfigurationElement conf : confArray) {
			// factory
			if (conf.getName().equals("factory")) { //$NON-NLS-1$
				result.add(new DefaultFunctionWizardDescriptor(conf));
			}
			// simple
			else if (conf.getName().equals("simple")) { //$NON-NLS-1$
				result.add(new SimpleFunctionWizardDescriptor(conf));
			}
		}
		
		return result;
	}

}
