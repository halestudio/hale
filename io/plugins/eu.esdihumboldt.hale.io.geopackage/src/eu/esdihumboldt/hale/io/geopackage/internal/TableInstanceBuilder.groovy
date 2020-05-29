/*
 * Copyright (c) 2020 wetransform GmbH
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

package eu.esdihumboldt.hale.io.geopackage.internal

import org.locationtech.jts.io.WKBReader

import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryMetadata
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType
import groovy.transform.CompileStatic
import mil.nga.geopackage.features.user.FeatureResultSet
import mil.nga.geopackage.user.UserResultSet


/**
 * Creates instances from {@link FeatureResultSet}s rows. Not thread safe as a shared
 * {@link InstanceBuilder} instance is used.
 * 
 * @author Simon Templer
 */
@CompileStatic
class TableInstanceBuilder {

	private final SimpleLog log
	private final InstanceBuilder builder
	//	private final CRSProvider crsProvider
	private final WKBReader wkbReader

	/**
	 * Default constructor. 
	 */
	public TableInstanceBuilder(/*CRSProvider crsProvider, */SimpleLog log) {
		super();
		//		this.crsProvider = crsProvider
		this.log = log

		builder = new InstanceBuilder(strictBinding: false)
		wkbReader = new WKBReader()
	}
	/**
	 * Create an instance with the given type from a row in a SQL result set.
	 * 
	 * @param type the instance type
	 * @param row the result set with the cursor at the row to create the
	 *   instance from
	 * @return the created instance
	 */
	Instance createInstance(TypeDefinition type, UserResultSet<?, ?, ?> row) {
		// create instance
		builder.createInstance(type) {
			// create properties
			Collection<PropertyDefinition> allProperties = (Collection<PropertyDefinition>) DefinitionUtil.getAllProperties(type) // Groovy CompileStatic can't deal properly with ? extends ...
			allProperties.each { PropertyDefinition property ->
				// get property value
				try {
					Object value = row.getResultSet().getObject(property.name.localPart)

					// geometry conversion
					if (value != null) {
						GeometryType gType = property.propertyType.getConstraint(GeometryType)
						if (gType.isGeometry() && row instanceof FeatureResultSet) {
							def geomData = ((FeatureResultSet) row).geometry

							// read JTS geometry from WKB
							def jtsGeom = wkbReader.read(geomData.wkbBytes)

							CRSDefinition crsDef = null
							// determine CRS
							GeometryMetadata geomMetadata = property.propertyType.getConstraint(GeometryMetadata)
							if (geomMetadata.getAuthName() && geomMetadata.srs) {
								// prefer code definition
								// always x then y (longitude first)
								crsDef = new CodeDefinition(geomMetadata.getAuthName() + ':' + geomMetadata.srs, true)
							}
							else {
								crsDef = new WKTDefinition(geomMetadata.srsText, null)
							}
							def geomProp = new DefaultGeometryProperty(crsDef, jtsGeom)

							//TODO also include geopackage original geometry?

							value = geomProp
						}
					}

					// create property
					if (value != null) {
						builder.createProperty(property.name.localPart, value)
					}
				} catch (Exception e) {
					// value could not be retrieved
					log.error("Could not retrieve value for column $property.name.localPart", e)
				}
			}
		}
	}

}
