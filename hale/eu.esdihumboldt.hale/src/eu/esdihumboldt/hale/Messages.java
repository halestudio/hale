// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2010 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.hale;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides means to access externalized strings.
 * @author Michel Kraemer
 */
public class Messages {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * Hidden constructor
	 */
	private Messages() {
		//nothing to do here
	}

	/**
	 * Get externalized string
	 * @param key the string's id
	 * @return the externalized string
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
