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

package eu.esdihumboldt.hale.common.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.core.io.service.ContentTypeService;
import eu.esdihumboldt.hale.common.core.io.service.internal.ContentTypeTracker;

/**
 * Bundle activator
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class CoreBundle implements BundleActivator {
	
	private ContentTypeTracker contentTypeService;
	
	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		contentTypeService = new ContentTypeTracker();
		// start tracking content types
		contentTypeService.start(context);
		// register service
		OsgiUtils.registerService(ContentTypeService.class, contentTypeService);
		
		// stuff to allow class loading FIXME move to osgi utils
		this.context = context;
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// unregister service
		OsgiUtils.unregisterService(contentTypeService);
		// stop tracking content types
		contentTypeService.stop();
	}
	
	private BundleContext context;
	
	/**
	 * @return the context
	 */
	public BundleContext getContext() {
		return context;
	}

}
