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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.spi.RegistryContributor;
import org.eclipse.core.runtime.spi.RegistryStrategy;
import org.eclipse.osgi.framework.util.Headers;

/**
 * 
 * The registry strategy that can be used in NO OSGi-env.
 * <p>
 * This class emulate RegistryStrategyOSGI
 * </p>
 * 
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class RegistryStrategyNonOSGI extends RegistryStrategy {

	private Object token;

	public RegistryStrategyNonOSGI(File[] storageDirs, boolean[] cacheReadOnly,
			Object token) {
		super(storageDirs, cacheReadOnly);
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.spi.RegistryStrategy#onStart(org.eclipse.core
	 * .runtime.IExtensionRegistry, boolean)
	 */
	public void onStart(IExtensionRegistry registry, boolean loadedFromCache) {
		// see EclipseBundleListener class (OSGi-env)
		long startGlobalTime = System.currentTimeMillis();
		long startTime = 0;

		int pluginXMLWithNoError = 0;
		int pluginXMLWithError = 0;
		int pluginXMLTotal = 0;
		try {
			if (DebugHelper.DEBUG) {
				DebugHelper.log("BEGIN RegistryStrategyNonOSGI#onStart");
			}
			super.onStart(registry, loadedFromCache);

			if (!(registry instanceof ExtensionRegistry)) {
				if (DebugHelper.DEBUG) {
					DebugHelper.log(
							"Impossible to load <plugin.xml>. IExtensionRegistry must be an instance of <"
									+ ExtensionRegistry.class.getName() + ">",
							1);
				}
				return;
			}

			ExtensionRegistry extensionRegistry = (ExtensionRegistry) registry;

			// Searching <plugin.xml> URL from the ClassLoader.
			if (DebugHelper.DEBUG) {
				startTime = System.currentTimeMillis();
				DebugHelper
						.log("Start searching <plugin.xml> URLs from the ClassLoader....",
								1);
			}
			ClassLoader cl = getClass().getClassLoader();
			Enumeration<URL> pluginURLs = Utils.getPluginXMLs(cl);
			if (pluginURLs == null || !pluginURLs.hasMoreElements()) {
				if (DebugHelper.DEBUG) {
					DebugHelper.log(
							"No <plugin.xml> founded into the ClassLoader", 1);
				}
				return;
			}
			if (DebugHelper.DEBUG) {
				DebugHelper.log(
						"End searching <plugin.xml> URLs from the ClassLoader with time="
								+ (System.currentTimeMillis() - startTime)
								+ "(ms)", 1);
			}
			// <plugin.xml> files are present into ClassLoader.
			// Searching <META-INF/MANIFEST.MF> URL from the ClassLoader.
			if (DebugHelper.DEBUG) {
				startTime = System.currentTimeMillis();
				DebugHelper
						.log("Start searching <META-INF/MANIFEST.MF> URLs from the ClassLoader....",
								1);
			}
			Map<String /* baseDir of the files */, URL /* of the MANIFEST.MF */> manifests = Utils
					.getManifestsMap(cl);
			if (DebugHelper.DEBUG) {
				DebugHelper.log(
						"End searching <META-INF/MANIFEST.MF> URLs from the ClassLoader with time="
								+ (System.currentTimeMillis() - startTime)
								+ "(ms)", 1);
			}

			if (DebugHelper.DEBUG) {
				startTime = System.currentTimeMillis();
				DebugHelper.log("Start loading <plugin.xml> ....", 1);
			}
			// Loop for each plugin.xml URL
			while (pluginURLs.hasMoreElements()) {
				URL url = pluginURLs.nextElement();
				pluginXMLTotal++;
				try {
					if (loadPluginXML(url, extensionRegistry, manifests)) {
						pluginXMLWithNoError++;
					} else {
						pluginXMLWithError++;
					}
				} catch (RuntimeException e) {
					pluginXMLWithError++;
					if (DebugHelper.DEBUG) {
						DebugHelper.logError(e);
					}
				}
			}
			if (DebugHelper.DEBUG) {
				DebugHelper.log(
						"End loading <plugin.xml> with time="
								+ (System.currentTimeMillis() - startTime)
								+ "(ms)", 1);
			}
		} finally {
			if (DebugHelper.DEBUG) {
				DebugHelper
						.log("END RegistryStrategyNonOSGI#onStart: plugin.xml [OK]=<"
								+ pluginXMLWithNoError
								+ "/"
								+ pluginXMLTotal
								+ ">, plugin.xml [ERROR]=<"
								+ pluginXMLWithError
								+ "/"
								+ pluginXMLTotal
								+ ">, time="
								+ (System.currentTimeMillis() - startGlobalTime)
								+ "(ms).");
			}
		}
	}

	/**
	 * Load the plugin.xml.
	 * 
	 * @param pluginManifest
	 * @param extensionRegistry
	 * @param manifests
	 * @return
	 */
	private boolean loadPluginXML(URL pluginManifest,
			ExtensionRegistry extensionRegistry, Map<String, URL> manifests) {
		long startTime = System.currentTimeMillis();
		if (pluginManifest == null) {
			return false;
		}

		InputStream is;
		try {
			is = new BufferedInputStream(pluginManifest.openStream());
		} catch (IOException ex) {
			is = null;
			if (DebugHelper.DEBUG) {
				DebugHelper.logError("<plugin.xml> [ERROR] : ("
						+ pluginManifest.getPath() + "): ", 1);
				DebugHelper.logError(ex);
			}
			return false;
		}

		// Search META-INF/MANIFEST.MF stored int the same plugin.xml folder.
		String baseDir = Utils.getBaseDir(pluginManifest,
				Constants.PLUGIN_MANIFEST);
		URL manifestURL = manifests.get(baseDir);
		if (manifestURL == null) {
			// META-INF/MANIFEST.MF doesn't exist for the plugin.xml
			// ignore it.
			if (DebugHelper.DEBUG) {
				DebugHelper.logError("<plugin.xml> [ERROR] : ("
						+ pluginManifest.getPath() + "): ", 1);
				DebugHelper.logError(
						"<META-INF/MANIFEST.MF> doesn't exist for the <plugin.xml>. <"
								+ baseDir
								+ "META-INF/MANIFEST.MF> not founded.", 2);
			}
			return false;
		}

		// MANIFEST.MF founded for the plugin.xml, Parse MANIFEST.MF.
		@SuppressWarnings("rawtypes")
		Headers headers = null;
		try {
			headers = Headers.parseManifest(manifestURL.openStream());
		} catch (Exception e) {
			if (DebugHelper.DEBUG) {
				DebugHelper.logError("<plugin.xml> [ERROR] : ("
						+ pluginManifest.getPath() + "): ", 1);
				DebugHelper.logError("Error while parsing MANIFEST.MF=<"
						+ manifestURL.getPath() + ">", 2);
				DebugHelper.logError(e);
			}
			return false;
		}

		// Get Bundle-SymbolicName from the MANIFEST.MF
		String symbolicName = (String) headers
				.get(Constants.BUNDLE_SYMBOLICNAME);
		if (Utils.isEmpty(symbolicName)) {
			if (DebugHelper.DEBUG) {
				DebugHelper.logError("<plugin.xml> [ERROR] : ("
						+ pluginManifest.getPath() + "): ", 1);
				DebugHelper.logError(
						"Cannot found <Bundle-SymbolicName> from the MANIFEST.MF=<"
								+ manifestURL.getPath() + ">", 2);
			}
			return false;
		}

		// Remove options from the Bundle-SymbolicName declaration.
		int index = symbolicName.indexOf(';');
		if (index != -1) {
			symbolicName = symbolicName.substring(0, index);
		}

		// Create IContributor
		RegistryContributor contributor = ContributorFactoryNonOSGI
				.createContributor(symbolicName);
		// Test if IContributor doesn't already exists.
		if (extensionRegistry.hasContributor(contributor)) {
			if (DebugHelper.DEBUG) {
				DebugHelper.logError("<plugin.xml> [ERROR] : ("
						+ pluginManifest.getPath() + "): ", 1);
				DebugHelper.logError(
						"Contributor with id=<" + contributor.getActualId()
								+ "> already exits.", 2);
			}
			return false;
		}

		ResourceBundle translationBundle = null;
		long timestamp = 0;

		// Parse the plugin.xml
		if (!extensionRegistry.addContribution(is, contributor, true,
				pluginManifest.getPath(), translationBundle, token, timestamp)) {
			if (DebugHelper.DEBUG) {
				DebugHelper.logError("<plugin.xml> [ERROR] : ("
						+ pluginManifest.getPath() + "): ", 1);
				DebugHelper.logError("Parsing problems with plugin.xml", 2);
			}
			return false;
		}

		
		if (DebugHelper.DEBUG) {
			DebugHelper.log("<plugin.xml> [OK] loaded with time="
								+ (System.currentTimeMillis() - startTime)
								+ "(ms) : (" + pluginManifest.getPath()
					+ ")", 1);
		}
		return true;
	}
}
