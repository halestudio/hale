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

import java.sql.SQLException

import javax.xml.namespace.QName

import org.locationtech.jts.io.WKBReader

import eu.esdihumboldt.hale.common.align.helper.EntityFinder
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryMetadata
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType
import eu.esdihumboldt.hale.io.geopackage.GeopackageSchemaBuilder
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

	private final Map<TypeDefinition, Collection<PropertyDefinition>> cachedProperties = [:]
	private final Map<TypeDefinition, QName> cachedGeometryProperty = [:]

	private final Set<String> noSuchColumnLogged = new HashSet()

	/**
	 * Default constructor. 
	 */
	public TableInstanceBuilder(/*CRSProvider crsProvider, */SimpleLog log) {
		super();
		//		this.crsProvider = crsProvider
		this.log = log

		builder = new InstanceBuilder(
				strictBinding: false,
				strictValueFlags: false // allow setting values even if no value is expected (mainly for use with XML schema and geometries)
				)
		wkbReader = new WKBReader()
	}

	/**
	 * Filter properties to remove duplicates (related to the local name)
	 * 
	 * @param properties the properties to filter
	 * @return the filtered properties that no longer contain duplicates related to the local name
	 */
	private Collection<PropertyDefinition> filterProperties(Collection<PropertyDefinition> properties) {
		def byLocalName = properties.groupBy { property -> property.name.localPart }

		byLocalName.collect { name, candidates ->
			// prefer property with empty namespace as in schema created by GeopackageSchemaReader
			def cand = candidates.find { it.name.namespaceURI == null || it.name.namespaceURI.isEmpty() }

			if (cand == null) {
				// prefer property with Geopackage namespace (because hale connect schema service applies the type/schema namespace to the properties as well)
				cand = candidates.find { it.name.namespaceURI == GeopackageSchemaBuilder.DEFAULT_NAMESPACE }
			}

			if (cand == null) {
				// prefer property that is mandatory
				cand = candidates.find { it.getConstraint(Cardinality).getMinOccurs() > 0 && !it.getConstraint(NillableFlag).isEnabled() }
			}

			if (cand == null) {
				// just pick the first one
				cand = candidates[0]
			}

			cand
		}
	}

	private Collection<PropertyDefinition> getProperties(TypeDefinition type) {
		return cachedProperties.computeIfAbsent(type) { TypeDefinition t ->
			Collection<PropertyDefinition> allProperties = (Collection<PropertyDefinition>) DefinitionUtil.getAllProperties(t) // Groovy CompileStatic can't deal properly with ? extends ...
			filterProperties(allProperties)
		}
	}

	private QName getGeometryProperty(TypeDefinition type) {
		return cachedGeometryProperty.computeIfAbsent(type) { TypeDefinition t ->
			// allow for geometry property types with choices
			int checkLevels = 3

			// create finder for geometry properties
			EntityFinder finder = new EntityFinder({ EntityDefinition entity ->
				// determine if the property classifies as
				if (entity.getDefinition() instanceof PropertyDefinition) {
					def propertyType = ((PropertyDefinition) entity.getDefinition()).getPropertyType()

					boolean isGeometry = propertyType.getConstraint(GeometryType).isGeometry()
					if (isGeometry) {
						return true
					}
				}

				false
			}, checkLevels)

			def parents = getProperties(type).collect { PropertyDefinition p ->
				AlignmentUtil.createEntityFromDefinitions(type, [p], SchemaSpaceID.SOURCE, null)
			}

			def candidates = finder.find(parents)

			if (candidates.empty) {
				null
			}
			else {
				// select candidate
				//XXX for now just use first one
				def name = candidates[0].propertyPath[0].child.name

				log.info("Identified property $name as geometry property for type ${type.name.localPart}")

				name
			}
		}
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
			Collection<PropertyDefinition> filteredProperties = getProperties(type)
			QName geometryProperty = getGeometryProperty(type)
			filteredProperties.each { PropertyDefinition property ->
				// get property value
				try {
					Object value = row.getResultSet().getObject(property.name.localPart)

					// geometry conversion
					if (value != null) {
						if (geometryProperty != null && geometryProperty.equals(property.getName()) && row instanceof FeatureResultSet) {
							def geomData = ((FeatureResultSet) row).geometry

							// read JTS geometry from WKB
							def jtsGeom = wkbReader.read(geomData.wkbBytes)

							CRSDefinition crsDef = null
							// determine CRS from schema (usually possible if the schema is the Geopackage schema)
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
						builder.createProperty(property.name.localPart, property.name.namespaceURI, value)
					}
				} catch (Exception e) {
					// value could not be retrieved
					if (e instanceof SQLException && e.message != null && e.message.contains('no such column')) {
						if (!noSuchColumnLogged.contains(property.name.localPart)) {
							// the column not being there is at max a warning, and he stacktrace is not important
							log.warn("Could not retrieve value for column ${property.name.localPart}: ${e.message}")

							// don't warn again
							noSuchColumnLogged.add(property.name.localPart)
						}
					}
					else {
						log.error("Could not retrieve value for column $property.name.localPart", e)
					}
				}
			}
		}
	}

}
