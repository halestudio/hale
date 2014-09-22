/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.csv;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.parameter.AbstractParameterValueDescriptor;

/**
 * Parameter value descriptor for the typename parameter.
 * 
 * @author Simon Templer
 */
public class TypenameParameterDescriptor extends AbstractParameterValueDescriptor {

	/**
	 * Default constructor.
	 */
	public TypenameParameterDescriptor() {
		super(null, Value.of(new QName("namespace", "localname").toString()));
	}

	@Override
	public String getSampleDescription() {
		return "The type name is represented like in the given example, with the namespace in curly braces.";
	}
}
