/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.ui.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceAdapter;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener;
import eu.esdihumboldt.hale.ui.HaleUI;

/**
 * Provides UI variables related to the {@link HaleConnectService}
 * 
 * @author Florian Esser
 */
public class HaleConnectSource extends AbstractSourceProvider {

	/**
	 * The name of the variable which value is <code>true</code> if there is a
	 * hale connect JWT available in the {@link HaleConnectService}.
	 */
	public static final String LOGIN_STATUS = "eu.esdihumboldt.hale.io.haleconnect.ui.login.status";

	private HaleConnectServiceListener listener;

	/**
	 * 
	 */
	public HaleConnectSource() {
		super();

		final HaleConnectService hcs = HaleUI.getServiceProvider()
				.getService(HaleConnectService.class);
		hcs.addListener(listener = new HaleConnectServiceAdapter() {

			@Override
			public void loginStateChanged(boolean loggedIn) {
				fireSourceChanged(ISources.WORKBENCH, LOGIN_STATUS, loggedIn);
			}
		});
	}

	/**
	 * @see org.eclipse.ui.ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState() {
		final Map<String, Object> result = new HashMap<String, Object>();

		final HaleConnectService hcs = HaleUI.getServiceProvider()
				.getService(HaleConnectService.class);

		result.put(LOGIN_STATUS, hcs.isLoggedIn());

		return result;
	}

	/**
	 * @see org.eclipse.ui.ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { LOGIN_STATUS };
	}

	/**
	 * @see org.eclipse.ui.ISourceProvider#dispose()
	 */
	@Override
	public void dispose() {
		final HaleConnectService hcs = HaleUI.getServiceProvider()
				.getService(HaleConnectService.class);
		hcs.removeListener(listener);
	}

}
