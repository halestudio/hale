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

package eu.esdihumboldt.hale.io.xsd.anytype

import groovy.transform.CompileStatic
import groovy.transform.Immutable

import javax.xml.namespace.QName


/**
 * Associates a schema property with a custom type association.
 * @author Simon Templer
 */
@CompileStatic
@Immutable
class CustomTypeContentAssociation {

	List<QName> property

	CustomTypeContent config
}
