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
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectSession;

/**
 * hale connect service
 * 
 * @author Florian Esser
 */
public class HaleConnectServiceImpl implements HaleConnectService {

	private final CopyOnWriteArraySet<HaleConnectServiceListener> listeners = new CopyOnWriteArraySet<HaleConnectServiceListener>();

	private String basePath = "https://users.haleconnect.com/v1";

	private HaleConnectSession session;

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#login(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean login(String username, String password) throws HaleConnectException {
		ApiClient apiClient = getApiClient();

		LoginApi loginApi = new LoginApi(apiClient);
		Credentials credentials = new Credentials();
		credentials.setUsername(Optional.ofNullable(username).orElse(""));
		credentials.setPassword(Optional.ofNullable(password).orElse(""));

		try {
			Token token = loginApi.login(credentials);
			if (token != null) {
				session = new HaleConnectSessionImpl(credentials.getUsername(), token.getToken());
				notifyLoginStateChanged();
			}
			else {
				clearSession();
			}
		} catch (ApiException e) {
			if (e.getCode() == 401) {
				clearSession();
			}
			else {
				throw new HaleConnectException(e.getMessage(), e);
			}
		}

		return isLoggedIn();
	}

	@Override
	public boolean verifyCredentials(String username, String password) throws HaleConnectException {
		Credentials credentials = new Credentials();
		credentials.setUsername(Optional.ofNullable(username).orElse(""));
		credentials.setPassword(Optional.ofNullable(password).orElse(""));

		try {
			return getLoginApi().login(credentials) != null;
		} catch (ApiException e) {
			if (e.getCode() == 401) {
				return false;
			}
			else {
				throw new HaleConnectException(e.getMessage(), e);
			}
		}
	}

	private ApiClient getApiClient() {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(basePath);
		return apiClient;
	}

	private LoginApi getLoginApi() {
		return new LoginApi(getApiClient());
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
	public HaleConnectSession getSession() {
		return session;
	}

	@Override
	public void clearSession() {
		this.session = null;
		notifyLoginStateChanged();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.haleconnect.HaleConnectService#isLoggedIn()
	 */
	@Override
	public boolean isLoggedIn() {
		return session != null;
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
