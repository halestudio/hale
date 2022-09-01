/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.common.core.parameter;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Parameter value descriptor for a list of qualified names.
 * 
 * @author Simon Templer
 */
public class QNameParameterValueDescriptor extends AbstractParameterValueDescriptor {

	/**
	 * Default constructor.
	 */
	public QNameParameterValueDescriptor() {
		super(Value.NULL, Value.of(new QName("http://namespace.example.com", "name")));
	}

}
