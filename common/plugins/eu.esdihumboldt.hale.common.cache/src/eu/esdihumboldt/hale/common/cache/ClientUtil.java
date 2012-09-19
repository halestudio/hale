/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.cache;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * HTTP client utilities
 * 
 * @author Simon Templer, Michel Krämer
 */
public class ClientUtil {

	/**
	 * Create a thread safe HTTP client
	 * 
	 * @return the created HTTP client
	 */
	public static DefaultHttpClient createThreadSafeHttpClient() {
		// create default scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, //$NON-NLS-1$
				PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, //$NON-NLS-1$
				SSLSocketFactory.getSocketFactory()));

		// create multi-threaded connection manager
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		ClientConnectionManager cm = new ThreadSafeClientConnManager(schemeRegistry);

		// create HTTP client
		return new DefaultHttpClient(cm, params);
	}

}
