/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.ui.internal;

import org.eclipse.jface.resource.ImageRegistry;

/**
 * hale connect images
 * 
 * @author Florian Esser
 */
@SuppressWarnings("javadoc")
public class HaleConnectImages {

	public static final String IMG_HCLOGO_PREFERENCES = "IMG_HCLOGO_PREFERENCES";

	public static final String IMG_HCLOGO_DIALOG = "IMG_HCLOGO_DIALOG";

	public static final String IMG_HCLOGO_ICON = "IMG_HCLOGO_ICON";

	/**
	 * Get the image registry
	 * 
	 * @return the image registry
	 */
	public static ImageRegistry getImageRegistry() {
		return HaleConnectUIPlugin.getDefault().getImageRegistry();
	}
}
