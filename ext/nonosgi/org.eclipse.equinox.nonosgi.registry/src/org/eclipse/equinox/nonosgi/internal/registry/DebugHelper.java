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

/**
 * Helper to debug None OSGi-env registry.
 */
public final class DebugHelper {

	private static final String PLUGIN_NAME = "org.eclipse.equinox.nonosgi.registry"; //$NON-NLS-1$
	private static final String OPTION_DEBUG = PLUGIN_NAME + "/debug"; //$NON-NLS-1$

	public static boolean DEBUG = false;

	static {
		try {
			// use qualified name for Activator to ensure that it won't
			// be loaded outside of this block
			DEBUG = Activator.getBooleanDebugOption(OPTION_DEBUG, false);
		} catch (NoClassDefFoundError noClass) {
			// no OSGi - OK
		}
	}

	/**
	 * Log message.
	 * 
	 * @param message
	 */
	public static void log(String message) {
		log(message, 0);
	}

	/**
	 * Log message with indent.
	 * 
	 * @param message
	 * @param indent
	 */
	public static void log(String message, int indent) {
		System.out.println(createMessage(message, indent));
	}

	/**
	 * Log error.
	 * 
	 * @param error
	 */
	public static void logError(String message) {
		logError(message, 0);
	}

	/**
	 * Log error with indent.
	 * 
	 * @param message
	 * @param indent
	 */
	public static void logError(String message, int indent) {
		System.err.println(createMessage(message, indent));
	}

	/**
	 * Log error exception.
	 * 
	 * @param e
	 */
	public static void logError(Throwable e) {
		e.printStackTrace(System.err);
		System.err.println();
	}

	/**
	 * Create message.
	 * 
	 * @param message
	 * @param indent
	 * @return
	 */
	private static String createMessage(String message, int indent) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			s.append("\t");
		}
		s.append("[");
		s.append(PLUGIN_NAME);
		s.append("] ");		
		if (message != null) {
			s.append(message);
		}
		return s.toString();
	}
}
