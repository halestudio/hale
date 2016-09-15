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
package org.eclipse.equinox.nonosgi.registry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.equinox.nonosgi.internal.registry.DebugHelper;
import org.eclipse.equinox.nonosgi.internal.registry.RegistryProviderNonOSGI;

/**
 * Helper class which returns {@link IExtensionRegistry} singleton switch the
 * environment (OSGi-env or none OSGi-env) :
 * 
 * <ul>
 * <li>into OSGi-env, returns the "standard" OSGi {@link IExtensionRegistry}
 * singleton which use Bundle Activator to load "plugin.xml".</li>
 * <li>into NONE OSGi-env, returns the {@link IExtensionRegistry} singleton
 * which load the whole "plugin.xml" founded from the shared ClassLoader.</li>
 * </ul>
 * 
 */
public class RegistryFactoryHelper {

	private static Boolean OSGI_ENV = null;

	public static IExtensionRegistry getRegistry() {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		if (registry != null) {
			// OSGi-env, returns the "standard" OSGi {@link
			// IExtensionRegistry} singleton (OR the cached IExtensionRegistry
			// already loaded).
			if (OSGI_ENV == null) {
				OSGI_ENV = true;
			}
		} else {
			try {
				OSGI_ENV = false;
				// NONE OSGi-env, load the whole "plugin.xml" founded from the
				// shared ClassLoader.
				registry = createNoOSGIRegistry();
			} catch (CoreException e) {
				// This error should never occurred.
				e.printStackTrace();
			}
		}
		if (DebugHelper.DEBUG) {
			// Debug mode, trace
			if (OSGI_ENV) {
				DebugHelper.log("Returns IExtensionRegistry from the OSGi-env. Thread=" + Thread.currentThread());
			} else {
				DebugHelper.log("Returns IExtensionRegistry from the NO OSGi-env. Thread=" + Thread.currentThread());
			}
		}
		return registry;
	}

	/**
	 * Create No OSG-env {@link IExtensionRegistry}. This method is synchronized
	 * to avoid loading twice the plugin.xml files with multi Thread context.
	 * 
	 * @return
	 * @throws CoreException
	 */
	private static synchronized IExtensionRegistry createNoOSGIRegistry()
			throws CoreException {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		if (registry != null) {
			// Registry was already created with another Thread.
			return registry;
		}
		RegistryFactory
				.setDefaultRegistryProvider(new RegistryProviderNonOSGI());
		return RegistryFactory.getRegistry();
	}
}
