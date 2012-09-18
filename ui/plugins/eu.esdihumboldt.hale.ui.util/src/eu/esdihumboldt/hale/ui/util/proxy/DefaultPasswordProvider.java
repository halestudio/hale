/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
