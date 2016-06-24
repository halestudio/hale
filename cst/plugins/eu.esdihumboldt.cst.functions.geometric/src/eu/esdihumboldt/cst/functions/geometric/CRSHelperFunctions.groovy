/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.geometric

import org.geotools.referencing.CRS

import eu.esdihumboldt.cst.functions.groovy.helper.spec.SpecBuilder
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition
import groovy.transform.CompileStatic

/**
 * CRS related helper functions.
 *  
 * @author Simon Templer
 */
class CRSHelperFunctions {

	/**
	 * Specification for the from function
	 */
	public static final eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification _from_spec = SpecBuilder.newSpec( //
	description: 'Retrieve a CRS definition from a given code.', //
	result: 'the CRS definition') {
		//
		code('The code of the coordinate reference system')
		longitudeFirst('If instead of the default an axis order with longitude first should be used, if applicable for the CRS',
		value: false)
	} //

	@CompileStatic
	static CRSDefinition _from(Map args) {
		String code = args.code as String
		boolean longitudeFirst = false
		if (args.containsKey('longitudeFirst')) {
			longitudeFirst = args['longitudeFirst'] as boolean
		}

		if (longitudeFirst) {
			def crs = CRS.decode(code, true) // not cached!
			new CodeDefinition(code, crs)
		}
		else {
			new CodeDefinition(code, null)
		}
	}

	/**
	 * Specification for the fromCode function
	 */
	public static final eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification _fromWKT_spec = SpecBuilder.newSpec( //
	description: 'Build a CRS definition from a well-known text (WKT) representation.', //
	result: 'the CRS definition') { //
		wkt('Well-known text definition of a coordinate reference system') } //

	@CompileStatic
	static CRSDefinition _fromWKT(def wkt) {
		new WKTDefinition(wkt as String, null)
	}

}
