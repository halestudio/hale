/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.groovy.internal;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService;
import eu.esdihumboldt.cst.functions.groovy.helper.extension.HelperFunctionsExtension;

/**
 * Bundle activator.
 * 
 * @author Simon Templer
 */
public class Activator implements BundleActivator {

	private ServiceRegistration<HelperFunctionsService> helperFunctionsReg;

	@Override
	public void start(BundleContext context) throws Exception {
		helperFunctionsReg = context.registerService(HelperFunctionsService.class,
				new HelperFunctionsExtension(), new Hashtable<String, Object>());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		helperFunctionsReg.unregister();
	}

}
