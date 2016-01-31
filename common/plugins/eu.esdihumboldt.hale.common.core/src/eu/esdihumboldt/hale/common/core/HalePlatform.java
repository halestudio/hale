/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentTypeManager;

import eu.esdihumboldt.hale.common.core.internal.CoreBundle;
import eu.esdihumboldt.hale.util.nonosgi.NonOsgiPlatform;

/**
 * Helper methods
 * 
 * @author Simon Templer
 */
public class HalePlatform {

	public static IContentTypeManager getContentTypeManager() {
		if (CoreBundle.isOsgi()) {
			return Platform.getContentTypeManager();
		}
		else {
			return NonOsgiPlatform.getContentTypeManager();
		}
	}

}
