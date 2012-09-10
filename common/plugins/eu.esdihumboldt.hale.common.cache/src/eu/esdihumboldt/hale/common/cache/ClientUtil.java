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
