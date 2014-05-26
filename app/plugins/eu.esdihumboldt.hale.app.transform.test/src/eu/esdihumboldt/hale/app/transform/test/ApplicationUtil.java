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

import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationException;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.util.tracker.ServiceTracker;

import eu.esdihumboldt.hale.app.transform.test.internal.Activator;

/**
 * TODO Type description
 * 
 * @author simon-local
 */
public class ApplicationUtil {

	/**
	 * Launch an application inside the current framework.
	 * 
	 * @param appId the application identifier
	 * @param argList the arguments
	 * @return the application return value
	 * @throws ApplicationException
	 * @throws InterruptedException
	 */
	public static int launchApplication(String appId, List<String> argList)
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
					return (int) handle.getExitValue(0);
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
