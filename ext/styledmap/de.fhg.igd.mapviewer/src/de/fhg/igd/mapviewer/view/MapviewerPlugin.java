/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.view;

import java.util.Hashtable;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.fhg.igd.mapviewer.concurrency.Concurrency;
import de.fhg.igd.mapviewer.concurrency.Executor;
import de.fhg.igd.mapviewer.concurrency.JobExecutor;

/**
 * Mapviewer plugin activator
 * 
 * @author Simon Templer
 */
public class MapviewerPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "de.fhg.igd.mapviewer"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static MapviewerPlugin _plugin;

	private ServiceRegistration<Executor> service;

	/**
	 * The constructor
	 */
	public MapviewerPlugin() {
		// nothing to do here
	}

	/**
	 * @see AbstractUIPlugin#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;
		service = context.registerService(Executor.class, new JobExecutor(),
				new Hashtable<String, Object>());
		Concurrency.getInstance().start(context);
	}

	/**
	 * @see AbstractUIPlugin#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		Concurrency.getInstance().stop();
		if (service != null) {
			service.unregister();
		}
		super.stop(context);
	}

	/**
	 * @return the shared instance
	 */
	public static MapviewerPlugin getDefault() {
		return _plugin;
	}

}
