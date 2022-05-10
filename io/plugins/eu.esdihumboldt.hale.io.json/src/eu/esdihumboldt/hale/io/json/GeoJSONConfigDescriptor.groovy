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

package eu.esdihumboldt.hale.io.json;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.parameter.AbstractParameterValueDescriptor
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema

/**
 * {@link GeoJSONConfig} parameter descriptor.
 * 
 * @author Simon Templer
 * @deprecated as of release 4.2.0 this class is deprecated because
 *             {@link InstanceToJson} is used to export the data into GeoJson
 *             format.
 */
@Deprecated
public class GeoJSONConfigDescriptor extends AbstractParameterValueDescriptor {

	/**
	 * Default constructor.
	 */
	public GeoJSONConfigDescriptor() {
		super(null, Value.of(createSampleConfig()));
	}

	private static GeoJSONConfig createSampleConfig() {
		// create dummy schema
		def type1
		def type2
		Schema schema = new SchemaBuilder().schema {
			type1 = WatercourseType(namespace: 'http://www.example.com/namespace') {
				//
				geometry(namespace: 'http://www.example.com/namespace')
			}
			type2 = StandingWater {
				//
				the_geom() //
			}
		}

		PropertyEntityDefinition property1 = new TypeEntityDefinition(type1, SchemaSpaceID.SOURCE, null).accessor().geometry as PropertyEntityDefinition
		PropertyEntityDefinition property2 = new TypeEntityDefinition(type2, SchemaSpaceID.SOURCE, null).accessor().the_geom as PropertyEntityDefinition

		GeoJSONConfig result = new GeoJSONConfig();

		result.addDefaultGeometry(type1, property1);
		result.addDefaultGeometry(type2, property2);

		return result;
	}

}
