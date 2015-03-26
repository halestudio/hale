/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.wfs;

/**
 * WFS related constants.
 * 
 * @author Simon Templer
 */
public interface WFSConstants {

	/**
	 * ID of the content type registered for WFS-T transactions.
	 */
	public static final String CONTENT_TYPE_ID_WFST = "eu.esdihumboldt.hale.io.wfs-t";

	/**
	 * Name of the parameter specifying the WFS version for I/O providers
	 * related to WFS.
	 */
	public static final String PARAM_WFS_VERSION = "wfsVersion";

}
