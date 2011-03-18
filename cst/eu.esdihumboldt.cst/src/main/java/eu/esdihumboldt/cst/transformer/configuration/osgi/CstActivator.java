/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.cst.transformer.configuration.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * A class that serves as Activator if the CST is used within a OSGI environment.
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 */
public class CstActivator 
	implements BundleActivator {
	
	private static BundleContext context = null;

	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		CstActivator.context = context;
		Class.forName(
				"eu.esdihumboldt.cst.transformer.configuration.osgi.OSGIPackageResolver"); //$NON-NLS-1$
		
		CstFunctionExtension.registerFunctions();
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		context = null;
	}
	
	/**
	 * Get the bundle context
	 * 
	 * @return the bundle context
	 */
	public static BundleContext getContext() {
		return context;
	}

}
