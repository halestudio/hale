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

package eu.esdihumboldt.hale.io.wfs.ui.getfeature

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.io.wfs.WFSVersion
import eu.esdihumboldt.hale.io.wfs.capabilities.BBox
import groovy.transform.CompileStatic


/**
 * Configuration class for {@link WFSGetFeatureWizard}. 
 * @author Simon Templer
 */
@CompileStatic
class WFSGetFeatureConfig {
	URI getFeatureUri
	WFSVersion version
	final Set<QName> typeNames = new HashSet()
	BBox bbox
	String bboxCrsUri
	Integer maxFeatures
	String resolveDepth
}
