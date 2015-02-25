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

package eu.esdihumboldt.hale.io.wfs.file;

import eu.esdihumboldt.hale.io.wfs.WFSVersion;

/**
 * Concrete class for writing a WFS 2.0 FeatureCollection. Needed so HALE can
 * determine without setting a parameter if an appropriate container can be
 * found.
 * 
 * @author Simon Templer
 */
public class WFS2FCWriter extends WFSFeatureCollectionWriter {

	/**
	 * Constructor.
	 */
	public WFS2FCWriter() {
		super();
		setWFSVersion(WFSVersion.V2_0_0);
	}

}
