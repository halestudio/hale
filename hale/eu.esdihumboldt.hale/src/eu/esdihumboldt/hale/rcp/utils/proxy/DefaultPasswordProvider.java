// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2011 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.hale.rcp.utils.proxy;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

/**
 * Password provider that provides a default password
 * @author Simon Templer
 */
public class DefaultPasswordProvider extends PasswordProvider {

	/**
	 * @see PasswordProvider#getPassword(IPreferencesContainer, int)
	 */
	@Override
	public PBEKeySpec getPassword(IPreferencesContainer container,
			int passwordType) {
		return new PBEKeySpec("rXMKSx2eAqshIh0j2JnyLCdc".toCharArray());
	}

}
