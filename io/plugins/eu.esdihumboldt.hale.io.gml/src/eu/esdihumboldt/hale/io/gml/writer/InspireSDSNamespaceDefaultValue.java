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

package eu.esdihumboldt.hale.io.gml.writer;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor;

/**
 * For testing Text based parameter complex value
 * 
 * @author Yasmina Kammeyer
 */
public class InspireSDSNamespaceDefaultValue implements ParameterValueDescriptor {

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor#getDefaultValue()
	 */
	@Override
	public Value getDefaultValue() {
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor#getDocumentationRepresentation()
	 */
	@Override
	public String getDocumentationRepresentation() {
		return "The namespace of the spatial dataset.";
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor#getSampleData()
	 */
	@Override
	public Value getSampleData() {
		return Value.of("plu:city:xyz");
	}

}
