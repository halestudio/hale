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

import java.util.Properties;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Bundle Activator for the NON OSGi registry.
 * 
 */
public class Activator implements BundleActivator {

	private static final String DEBUG_PROPERTY = "org.eclipse.equinox.nonosgi.registry.debug";

	/**
	 * The singleton Activator.
	 */
	private static Activator activator;

	/**
	 * OSGi {@link ServiceTracker} to retrieve the Eclipse OSGi service
	 * {@link DebugOptions}.
	 */
	private ServiceTracker debugTracker = null;

	/**
	 * The Bundle context.
	 */
	private BundleContext bundleContext;

	private static Properties nonosgiregistryProps = null;

	public Activator() {
		Activator.activator = this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		this.bundleContext = null;
	}

	/**
	 * Returns the Bundle context.
	 * 
	 * @return
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * Returns the singleton {@link Activator}.
	 * 
	 * @return
	 */
	public static Activator getDefault() {
		return Activator.activator;
	}

	/**
	 * Return the debug value of the option if founded otherwise return
	 * defaultValue.
	 * 
	 * @param option
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBooleanDebugOption(String option,
			boolean defaultValue) {
		// 1) search param from JVM
		String s = System.getProperty(DEBUG_PROPERTY);
		if (!Utils.isEmpty(s)) {
			return Utils.isTrue(s, defaultValue);
		}
		// 2) search param from nonosgiregistry.properties files.
		if (nonosgiregistryProps == null) {
			nonosgiregistryProps = Utils.load(Activator.class.getClassLoader());
			s = nonosgiregistryProps.getProperty(DEBUG_PROPERTY);
			if (!Utils.isEmpty(s)) {
				return Utils.isTrue(s, defaultValue);
			}
		}

		// 3) Search param from OSGi options
		Activator activator = getDefault();
		if (activator == null)
			// No OSGi context
			return defaultValue;
		BundleContext myBundleContext = activator.bundleContext;
		if (myBundleContext == null)
			return defaultValue;

		// Search the DebugOptions OSGi service
		if (getDefault().debugTracker == null) {
			getDefault().debugTracker = new ServiceTracker(
					getDefault().bundleContext, DebugOptions.class.getName(),
					null);
			getDefault().debugTracker.open();
		}

		DebugOptions options = (DebugOptions) getDefault().debugTracker
				.getService();
		if (options != null) {
			// get the value of the option by using OSGi service DebugOptions
			return Utils.isTrue(options.getOption(option), defaultValue);
		}
		return defaultValue;
	}
}
