/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.nonosgi.internal.registry;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utilities.
 * 
 */
public class Utils {

	private static final String TRUE = "true";

	/**
	 * Properties files (coming from fragment linked to this bundle) which
	 * configure NO OSGi-env.
	 */
	private static final String NONOSGIREGISTRY_PROPERTIES = "nonosgiregistry.properties";
	
	/**
	 * Return a map of the whole MANIFEST.MF founded into the ClassPath. The key
	 * map is the baseDir of the URI of the MANIFEST.MF file.
	 * 
	 * @param cl
	 * @return
	 */
	public static Map<String, URL> getManifestsMap(ClassLoader cl) {
		Enumeration<URL> urls = getManifests(cl);
		if (urls == null || !urls.hasMoreElements()) {
			return Collections.emptyMap();
		}
		String baseDir = null;
		Map<String, URL> manifests = new HashMap<String, URL>();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			if (url != null) {
				baseDir = getBaseDir(url, Constants.OSGI_BUNDLE_MANIFEST);
				manifests.put(baseDir, url);
			}
		}
		return manifests;
	}

	/**
	 * Returns baseDir of the URL.
	 * 
	 * @param url
	 * @param resourcePath
	 * @return
	 */
	public static String getBaseDir(URL url, String resourcePath) {
		String path = url.getPath();
		return path.substring(0, path.length() - resourcePath.length());
	}

	/**
	 * Returns the MANIFEST.MF founded from the ClassLoader.
	 * 
	 * @param cl
	 * @return
	 */
	public static Enumeration<URL> getManifests(ClassLoader cl) {
		return getResources(cl, Constants.OSGI_BUNDLE_MANIFEST);
	}

	/**
	 * Returns the plugin.xml founded from the ClassLoader.
	 * 
	 * @param cl
	 * @return
	 */
	public static Enumeration<URL> getPluginXMLs(ClassLoader cl) {
		return getResources(cl, Constants.PLUGIN_MANIFEST);
	}

	/**
	 * Returns a resources founded from the ClassLoader.
	 * 
	 * @param cl
	 * @param resourcePath
	 * @return
	 */
	private static Enumeration<URL> getResources(ClassLoader cl,
			String resourcePath) {
		try {
			return cl == null ? ClassLoader.getSystemResources(resourcePath)
					: cl.getResources(resourcePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * Checks if a String is empty ("") or null.
	 * </p>
	 * 
	 * @param value
	 *            the String to check, may be null
	 * @return <code>true</code> if the String is empty or null
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.length() < 1;
	}

	/**
	 * Return true if value is not null and equals to "true", otherwise returns
	 * defaultValue.
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static boolean isTrue(String value, boolean defaultValue) {
		if (value != null)
			return value.equalsIgnoreCase(TRUE); //$NON-NLS-1$
		return defaultValue;
	}

	/**
	 * Load springclrfactory.properties from OSGi fragments linked to this
	 * bundle..
	 * 
	 * @param cl
	 * @return
	 */
	public static Properties load(ClassLoader cl) {

		Properties nonosgiregistryProps = new Properties();

		Enumeration<URL> nonosgiregistryProperties = null;
		try {
			nonosgiregistryProperties = cl == null ? ClassLoader
					.getSystemResources(NONOSGIREGISTRY_PROPERTIES) : cl
					.getResources(NONOSGIREGISTRY_PROPERTIES);
		} catch (IOException e) {
			if (DebugHelper.DEBUG) {
				DebugHelper.logError(e);
			}
		}

		while (nonosgiregistryProperties.hasMoreElements()) {
			URL url = nonosgiregistryProperties.nextElement();
			if (url != null) {
				try {
					nonosgiregistryProps.load(url.openStream());
				} catch (IOException e) {
					if (DebugHelper.DEBUG) {
						DebugHelper.logError(e);
					}
				}
			}
		}
		return nonosgiregistryProps;
	}
}
