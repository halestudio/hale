// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2011 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.hale.rcp.utils.proxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.Proxy.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

/**
 * Proxy utility methods
 * @author Simon Templer
 */
public class ProxyUtil {
	
	private static Logger _log = Logger.getLogger(ProxyUtil.class);
	
	private static boolean initialized = false;
	
	private static final Set<Runnable> initializers = new HashSet<Runnable>();
	
	/**
	 * Tries to find the system's proxy configuration for a given URI
	 * @param uri the URI
	 * @return the proxy configuration (host and port)
	 */
	public static Proxy findProxy(URI uri) {
		init();
		
		//BUGFIX: Don't use this property since it makes the connection hang!
		//  Rather set the proxy through the system properties
		//  "http.proxyHost" and "http.proxyPort".
		//System.setProperty("java.net.useSystemProxies", "true");
		
		List<Proxy> proxies = ProxySelector.getDefault().select(uri);
		if (proxies != null && proxies.size() > 0) {
			for (Proxy proxy : proxies) {
				if (proxy.type() == Proxy.Type.HTTP) {
					return proxy;
				}
			}
		}
		
		return Proxy.NO_PROXY;
		
		/*
		 * The following code is obsolete
		 * System properties are handled correctly by proxy selector
		 * The only thing that the code supports additionally is setting
		 * using a proxy for the host that is the proxy host 
		 */
		/*String strProxyHost = System.getProperty("http.proxyHost");
		String strProxyPort = System.getProperty("http.proxyPort");
		String strNonProxyHosts = System.getProperty("http.nonProxyHosts");
		String[] nonProxyHosts;
		if (strNonProxyHosts != null) {
			nonProxyHosts = strNonProxyHosts.split("\\|");
		} else {
			nonProxyHosts = new String[0];
		}
		
		if (strProxyHost != null && strProxyPort != null) {
			boolean noProxy = false;
			for (int i = 0; i < nonProxyHosts.length; ++i) {
				if (nonProxyHosts[i].equalsIgnoreCase(uri.getHost())) {
					noProxy = true;
					break;
				}
			}
			
			if (!noProxy) {
				int proxyPort = Integer.parseInt(strProxyPort);
				return new InetSocketAddress(strProxyHost, proxyPort);
			}
		}
		
		return null;*/
	}

	/**
	 * Set-up the given HTTP client to use the given proxy
	 * 
	 * @param client the HTTP client
	 * @param proxy the proxy
	 */
	public static void setupClient(DefaultHttpClient client, Proxy proxy) {
		init();
		
		// check if proxy shall be used
		if (proxy != null && proxy.type() == Type.HTTP) {
        	InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();
        	
        	// set the proxy
        	HttpHost proxyHost = new HttpHost("localhost", proxyAddress.getPort());
        	client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
        	
        	String user = System.getProperty("http.proxyUser");
        	String password = System.getProperty("http.proxyPassword");
        	boolean useProxyAuth = user != null && !user.isEmpty();
        	
        	if (useProxyAuth) {
        		// set the proxy credentials
	        	client.getCredentialsProvider().setCredentials(
	                    new AuthScope(proxyAddress.getHostName(), proxyAddress.getPort()), 
	                    new UsernamePasswordCredentials(user, password));
        	}
        	
        	_log.trace("Set proxy to " + proxyAddress.getHostName() + ":" +
        			proxyAddress.getPort() + ((useProxyAuth)?(" as user " + user):("")));
        }
		else {
			// unset proxy
			client.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
	}
	
	/**
	 * Add a proxy initializer. It will be called before the
	 * first proxy usage
	 * 
	 * @param initializer the initializer
	 */
	public static void addInitializer(Runnable initializer) {
		synchronized (initializers) {
			if (initialized) {
				try {
					initializer.run();
				} catch (Exception e) {
					_log.error("Error executing proxy initializer", e);
				}
			}
			else {
				initializers.add(initializer);
			}
		}
	}

	private static void init() {
		synchronized (initializers) {
			if (!initialized) {
				for (Runnable initializer : initializers) {
					try {
						initializer.run();
					} catch (Exception e) {
						_log.error("Error executing proxy initializer", e);
					}
				}
				
				initializers.clear();
				
				initialized = true;
			}
		}
	}

}
