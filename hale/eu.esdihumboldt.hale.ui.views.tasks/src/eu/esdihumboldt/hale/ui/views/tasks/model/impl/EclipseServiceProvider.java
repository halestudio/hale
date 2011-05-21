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

package eu.esdihumboldt.hale.ui.views.tasks.model.impl;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;

/**
 * Service provider that retrieves services using {@link PlatformUI}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class EclipseServiceProvider implements ServiceProvider {

	/**
	 * @see ServiceProvider#getService(Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> serviceType) {
		return (T) PlatformUI.getWorkbench().getService(serviceType);
	}

}
