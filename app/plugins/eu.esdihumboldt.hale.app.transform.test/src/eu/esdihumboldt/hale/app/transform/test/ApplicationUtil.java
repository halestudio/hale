/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.app.transform.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationException;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.util.tracker.ServiceTracker;

import eu.esdihumboldt.hale.app.transform.test.internal.Activator;

/**
 * Utilities for launching applications.
 * 
 * @author Simon Templer
 */
public class ApplicationUtil {

	/**
	 * Launch an application.
	 * 
	 * @param application the application instance
	 * @param argList the application arguments
	 * @return the application return code
	 * @throws Exception if the application exits with an exception
	 */
	public static Object launchSyncApplication(IApplication application, final List<String> argList)
			throws Exception {
		String[] args = argList.size() == 0 ? null : (String[]) argList.toArray(new String[argList
				.size()]);
		final Map<String, Object> launchArgs = new HashMap<>(1);
		if (args != null) {
			launchArgs.put(IApplicationContext.APPLICATION_ARGS, args);
		}

		IApplicationContext context = new IApplicationContext() {

			@Override
			public void setResult(Object result, IApplication application) {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getBrandingProperty(String key) {
				return null;
			}

			@Override
			public String getBrandingName() {
				return null;
			}

			@Override
			public String getBrandingId() {
				return null;
			}

			@Override
			public String getBrandingDescription() {
				return null;
			}

			@Override
			public Bundle getBrandingBundle() {
				return null;
			}

			@Override
			public String getBrandingApplication() {
				return null;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public Map getArguments() {
				return launchArgs;
			}

			@Override
			public void applicationRunning() {
				// anything to do?
			}
		};
		return application.start(context);
	}

	/**
	 * Launch an application inside the current framework.
	 * 
	 * Please note that launching an application like this may fail if another
	 * application is running (that is a global singleton).
	 * 
	 * @param appId the application identifier
	 * @param argList the arguments
	 * @return the application return value
	 * @throws ApplicationException if the application cannot be launched
	 * @throws InterruptedException if the thread was interrupted while waiting
	 *             for the application termination
	 */
	public static Object launchApplication(String appId, List<String> argList)
			throws ApplicationException, InterruptedException {
		BundleContext context = Activator.getContext();
		ServiceTracker<ApplicationDescriptor, ?> applicationDescriptors = new ServiceTracker<>(
				context, ApplicationDescriptor.class.getName(), null);
		applicationDescriptors.open();
		try {
			ServiceReference<ApplicationDescriptor> application = getApplication(
					applicationDescriptors.getServiceReferences(), appId,
					ApplicationDescriptor.APPLICATION_PID, false);
			if (application == null) {
				throw new IllegalStateException("\"" + appId + "\" does not exist or is ambigous."); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				String[] args = argList.size() == 0 ? null : (String[]) argList
						.toArray(new String[argList.size()]);
				try {
					Map<String, Object> launchArgs = new HashMap<>(1);
					if (args != null) {
						launchArgs.put(IApplicationContext.APPLICATION_ARGS, args);
					}
					ApplicationDescriptor appDesc = (context.getService(application));
					ApplicationHandle handle = appDesc.launch(launchArgs);
					return handle.getExitValue(0);
				} finally {
					context.ungetService(application);
				}
			}
		} finally {
			applicationDescriptors.close();
		}
	}

	private static ServiceReference<ApplicationDescriptor> getApplication(
			ServiceReference<ApplicationDescriptor>[] apps, String targetId, String idKey,
			boolean perfectMatch) {
		if (apps == null || targetId == null)
			return null;

		ServiceReference<ApplicationDescriptor> result = null;
		boolean ambigous = false;
		for (int i = 0; i < apps.length; i++) {
			String id = (String) apps[i].getProperty(idKey);
			if (targetId.equals(id))
				return apps[i]; // always return a perfect match
			if (perfectMatch)
				continue;
			if (id.indexOf(targetId) >= 0) {
				if (result != null)
					ambigous = true;
				result = apps[i];
			}
		}
		return ambigous ? null : result;
	}

}
