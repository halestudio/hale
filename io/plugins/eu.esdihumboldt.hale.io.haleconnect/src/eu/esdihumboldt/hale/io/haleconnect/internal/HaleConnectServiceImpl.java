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

package eu.esdihumboldt.hale.io.haleconnect.internal;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

import com.haleconnect.api.user.v1.ApiClient;
import com.haleconnect.api.user.v1.ApiException;
import com.haleconnect.api.user.v1.api.LoginApi;
import com.haleconnect.api.user.v1.model.Credentials;
import com.haleconnect.api.user.v1.model.Token;

import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener;

/**
 * hale connect service
 * 
 * @author Florian Esser
 */
public class HaleConnectServiceImpl implements HaleConnectService {

	private final CopyOnWriteArraySet<HaleConnectServiceListener> listeners = new CopyOnWriteArraySet<HaleConnectServiceListener>();

	private String basePath = "https://users.haleconnect.com/v1";
	private Token activeToken;

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#login(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean login(String username, String password) throws HaleConnectException {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(basePath);

		LoginApi loginApi = new LoginApi(apiClient);

		Credentials credentials = new Credentials();
		credentials.setUsername(Optional.ofNullable(username).orElse(""));
		credentials.setPassword(Optional.ofNullable(password).orElse(""));

		try {
			activeToken = loginApi.login(credentials);
		} catch (ApiException e) {
			if (e.getCode() == 401) {
				activeToken = null;
			}
			else {
				throw new HaleConnectException(e.getMessage(), e);
			}
		}

		notifyLoginStateChanged();
		return isLoggedIn();
	}

	/**
	 * 
	 */
	private void notifyLoginStateChanged() {
		for (HaleConnectServiceListener listener : listeners) {
			listener.loginStateChanged(isLoggedIn());
		}
	}

	@Override
	public String getToken() {
		if (activeToken == null) {
			return null;
		}

		return activeToken.getToken();
	}

	@Override
	public void clearToken() {
		this.activeToken = null;
		notifyLoginStateChanged();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#isLoggedIn()
	 */
	@Override
	public boolean isLoggedIn() {
		return activeToken != null;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#addListener(eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener)
	 */
	@Override
	public void addListener(HaleConnectServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#removeListener(eu.esdihumboldt.hale.io.haleconnect.HaleConnectServiceListener)
	 */
	@Override
	public void removeListener(HaleConnectServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#setBasePath(java.lang.String)
	 */
	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}
