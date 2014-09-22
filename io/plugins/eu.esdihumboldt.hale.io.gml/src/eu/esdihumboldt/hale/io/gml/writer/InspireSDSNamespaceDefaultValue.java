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

	@Override
	public Value getDefaultValue() {
		return Value.of("");
	}

	@Override
	public String getDefaultDescription() {
		return null;
	}

	@Override
	public String getSampleDescription() {
		return "Example how an URI-based namespace for the GDI-DE registry could look like.";
	}

	@Override
	public Value getSampleData() {
		return Value.of("http://gdi-de.org/oid/de.bund.bkg");
	}

}
