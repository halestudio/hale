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

import java.io.File;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.spi.IRegistryProvider;
import org.eclipse.core.runtime.spi.RegistryStrategy;

/**
 * Provider for {@link IExtensionRegistry} into No OSGi-env.
 * 
 * <p>
 * This class emulate the
 * org.eclipse.core.internal.registry.osgi.RegistryProviderOSGI which use OSGi
 * services to retrieve an instance of {@link IExtensionRegistry}.
 * </p>
 * 
 */
public class RegistryProviderNonOSGI implements IRegistryProvider {

	private Object masterRegistryKey = new Object();
	private Object userRegistryKey = new Object();

	// Cache the regisrty
	private IExtensionRegistry registry = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.spi.IRegistryProvider#getRegistry()
	 */
	public IExtensionRegistry getRegistry() {
		if (registry != null)
			return registry;

		synchronized (this) {
			// FIXME : study if theses parameters must be filled???
			File[] storageDirs = null;
			boolean[] cacheReadOnly = null;

			// Create an instance of IExtensionRegistry.
			// Into OSGi-env (see RegistryProviderOSGI), it use OSGi
			// ServiceTracker
			// to retrieve the instance of IExtensionRegistry (see
			// org.eclipse.core.internal.registry.osgi.Activator#startRegistry()).

			// To create an instance of IExtensionRegistry :
			// 1. Create an instance of RegistryStrategy
			// 2. Create an instance of IExtensionRegistry by using the instance
			// of
			// RegistryStrategy

			// 1. Create an instance of RegistryStrategy for no OSGi-env.
			RegistryStrategy strategy = new RegistryStrategyNonOSGI(
					storageDirs, cacheReadOnly, masterRegistryKey);

			// 2. Create an instance of IExtensionRegistry by using the instance
			// of
			// RegistryStrategy
			registry = RegistryFactory.createRegistry(strategy,
					masterRegistryKey, userRegistryKey);
		}
		return registry;
	}

}
