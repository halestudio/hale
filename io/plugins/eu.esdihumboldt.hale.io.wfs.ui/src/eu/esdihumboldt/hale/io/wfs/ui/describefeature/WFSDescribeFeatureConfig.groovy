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

package eu.esdihumboldt.hale.io.wfs.ui.describefeature

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.io.wfs.WFSVersion
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities
import eu.esdihumboldt.hale.io.wfs.ui.capabilities.HasCapabilities
import groovy.transform.CompileStatic


/**
 * Configuration class for {@link WFSDescribeFeatureWizard}. 
 * @author Simon Templer
 */
@CompileStatic
class WFSDescribeFeatureConfig implements HasCapabilities {
	WFSCapabilities capabilities
	URI describeFeatureUri
	WFSVersion version
	final Set<QName> typeNames = new HashSet()
}
