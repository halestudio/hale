/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.geometry.impl.internal;

import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionFactory;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;

/**
 * Factory for {@link CodeDefinition}s
 * 
 * @author Simon Templer
 */
public class WKTDefinitionFactory implements CRSDefinitionFactory<WKTDefinition> {

	/**
	 * @see CRSDefinitionFactory#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return "wkt";
	}

	/**
	 * @see CRSDefinitionFactory#getObjectClass()
	 */
	@Override
	public Class<WKTDefinition> getObjectClass() {
		return WKTDefinition.class;
	}

	/**
	 * @see CRSDefinitionFactory#parse(String)
	 */
	@Override
	public WKTDefinition parse(String value) {
		return new WKTDefinition(value, null);
	}

	/**
	 * @see CRSDefinitionFactory#asString(Object)
	 */
	@Override
	public String asString(WKTDefinition crsDef) {
		return crsDef.getWkt();
	}

}
