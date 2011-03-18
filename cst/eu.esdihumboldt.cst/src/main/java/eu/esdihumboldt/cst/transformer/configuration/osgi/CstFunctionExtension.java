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
package eu.esdihumboldt.cst.transformer.configuration.osgi;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;

/**
 * Utilities for the CST function extension
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CstFunctionExtension {
	
	private static final Logger log = Logger.getLogger(CstFunctionExtension.class);
	
	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.cst.transformer.CstFunction"; //$NON-NLS-1$
	
	/**
	 * Register the functions defined by the extension
	 */
	public static void registerFunctions() {
		IConfigurationElement[] confArray = Platform.getExtensionRegistry().getConfigurationElementsFor(ID);
		
		CstFunctionFactory ff = CstFunctionFactory.getInstance();
		
		for (IConfigurationElement conf : confArray) {
			if (conf.getName().equals("java")) { //$NON-NLS-1$
				// java implementation
				String pkg = conf.getAttribute("package"); //$NON-NLS-1$
				log.info("Registering functions for package: " + pkg); //$NON-NLS-1$
				ff.registerCstPackage(pkg);
			}
			else if (conf.getName().equals("wps")) { //$NON-NLS-1$
				// wps
				String describeProcessUrl = conf.getAttribute("url"); //$NON-NLS-1$
				log.info("Registering function for WPS: " + describeProcessUrl); //$NON-NLS-1$
				//TODO register wps function
			}
		}
	}

}
