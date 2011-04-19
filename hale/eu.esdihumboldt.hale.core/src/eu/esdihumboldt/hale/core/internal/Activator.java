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

package eu.esdihumboldt.hale.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.fhg.igd.osgi.util.OsgiUtils;

import eu.esdihumboldt.hale.core.io.service.ContentTypeService;
import eu.esdihumboldt.hale.core.io.service.internal.ContentTypeTracker;

/**
 * Bundle activator
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class Activator implements BundleActivator {
	
	private ContentTypeTracker contentTypeService;

	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		// do nothing
		
		contentTypeService = new ContentTypeTracker();
		// start tracking content types
		contentTypeService.start(context);
		// register service
		OsgiUtils.registerService(ContentTypeService.class, contentTypeService);
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// unregister service
		OsgiUtils.unregisterService(contentTypeService);
		// stop tracking content types
		contentTypeService.start(context);
	}

}
