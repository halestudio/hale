/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.util.proxy;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

/**
 * Password provider that provides a default password
 * 
 * @author Simon Templer
 */
public class DefaultPasswordProvider extends PasswordProvider {

	/**
	 * @see PasswordProvider#getPassword(IPreferencesContainer, int)
	 */
	@Override
	public PBEKeySpec getPassword(IPreferencesContainer container, int passwordType) {
		return new PBEKeySpec("rXMKSx2eAqshIh0j2JnyLCdc".toCharArray()); //$NON-NLS-1$
	}

}
