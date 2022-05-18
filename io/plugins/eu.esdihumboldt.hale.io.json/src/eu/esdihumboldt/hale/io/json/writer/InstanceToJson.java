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

package eu.esdihumboldt.hale.io.json.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.MathTransform;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.cst.functions.geometric.GeometryHelperFunctions;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.geometry.WindingOrder;

/**
 * Class to generate instance to JSON.
 * 
 * @author Simon Templer
 */
public class InstanceToJson implements InstanceJsonConstants {

	private final GeometryJSON geometryJson;
	private final NamespaceManager namespaces;
	private final boolean useGeoJsonFeatures;

	// GeoJson expects WGS 84 with lon/lat (see
	// https://tools.ietf.org/html/rfc7946)
	private final CRSDefinition targetCrs = new CodeDefinition("EPSG:4326", true);

	private final TransformCache transformCache = new TransformCache();

	/**
	 *
	 * Note: The GeoJson output follows the RFC SPEC but extends it similar to
	 * the draft version 6 by attributes for namespace prefix definitions and
	 * specific feature type: http://wiki.geojson.org/GeoJSON_draft_version_6
	 *
	 * @param geoJson if the output should be valid GeoJson output
	 */
	public InstanceToJson(boolean geoJson) {
		this(geoJson, new IgnoreNamespaces() /* new JsonNamespaces() */, 7);
	}

	/**
	 *
	 * @param geoJson geojson
	 * @param namespaces namespace
	 * @param decimals the number of decimals to use when encoding floating
	 *            point numbers
	 */
	public InstanceToJson(boolean geoJson, NamespaceManager namespaces, int decimals) {
		super();
		this.useGeoJsonFeatures = geoJson;
		this.namespaces = namespaces;
		this.geometryJson = new GeometryJSON(decimals);

		if (geoJson) {
			// XXX should GeoJson namespace be the namespace w/o prefix?
//      namespaces.setPrefix(NAMESPACE_GEOJSON, "");
		}
	}

	/**
	 * @return namespace
	 */
	public NamespaceManager getNamespaces() {
		return namespaces;
	}

	/**
	 * @param <T> type
	 * @param writer writer
	 * @param prettyPrint true to pretty print
	 * @param handler json generator
	 * @return json generator
	 * @throws IOException exception
	 */
	public static <T> T withJsonGenerator(Writer writer, boolean prettyPrint,
			Function<JsonGenerator, T> handler) throws IOException {
		JsonFactory jsonFactory = new JsonFactory();

		try (JsonGenerator jsonGen = jsonFactory.createJsonGenerator(writer)) {
			if (prettyPrint) {
				jsonGen.useDefaultPrettyPrinter();
			}
			T result = handler.apply(jsonGen);
			jsonGen.flush();
			return result;
		}
	}

	/**
	 * Writes a collection of instances as Json
	 *
	 * @param jsonGen the Json generator
	 * @param instances the collection of instances
	 * @param log the log
	 * @throws IOException if writing the instance collection to Json fails
	 */
	public void writeCollection(JsonGenerator jsonGen, InstanceCollection instances, SimpleLog log)
			throws IOException {

		if (useGeoJsonFeatures) {
			// GeoJson

			jsonGen.writeStartObject();

			jsonGen.writeStringField("type", "FeatureCollection");

			// TODO support use case with custom crs? (not allowed in GeoJson
			// spec)

			jsonGen.writeArrayFieldStart("features");
		}
		else {
			// simple array

			jsonGen.writeStartArray();
		}

		// iterate through Instances
		try (ResourceIterator<Instance> itInstance = instances.iterator()) {
			while (itInstance.hasNext()) {
				Instance instance = itInstance.next();
				writeInstance(jsonGen, instance, Placement.MEMBER, log);
			}
		}
		jsonGen.writeEndArray();

		if (useGeoJsonFeatures) {
			// namespaces -> this is done at the end to be sure to have all
			// namespace prefixes
			writeNamespaces(jsonGen);

			// GeoJson end
			jsonGen.writeEndObject();
		}
	}

	/**
	 * Write an instance to Json.
	 *
	 * @param jsonGen the Json generator
	 * @param instance the instance to write
	 * @param log the log
	 * @throws IOException IOException
	 */
	public void writeInstance(JsonGenerator jsonGen, Instance instance, SimpleLog log)
			throws IOException {
		writeInstance(jsonGen, instance, Placement.ROOT, log);
	}

	/**
	 * Write an instance to Json.
	 *
	 * @param jsonGen the Json generator
	 * @param instance the instance to write
	 * @param placement the placement of the instance
	 * @param log the log
	 * @throws IOException exception
	 */
	protected void writeInstance(JsonGenerator jsonGen, Instance instance, Placement placement,
			SimpleLog log) throws IOException {
		TypeDefinition type = instance.getDefinition();
		boolean onlyValue = false;
		if (Placement.VALUE.equals(placement) && type != null) {
			if (type.getChildren().isEmpty()) {
				// no children defined -> can by schema only be value
				onlyValue = true;
				// TODO also allow to break schema here? introduce setting?
			}
			else {
				// may have children - usually properties should be written

				// special handling: geometry fields
				boolean isGeometryType = type.getConstraint(GeometryType.class).isGeometry();
				boolean valueOrAugmented = type.getConstraint(HasValueFlag.class).isEnabled()
						|| type.getConstraint(AugmentedValueFlag.class).isEnabled();
				if (isGeometryType && valueOrAugmented) {
					onlyValue = true;
				}

				// TODO also detect geometry property types?! retrieve geometry
				// in that case?
			}
		}

		if (Placement.VALUE.equals(placement) && onlyValue) {
			// value may only be written directly if placement is value
			writeValue(jsonGen, instance.getValue(), log);
		}
		else {
			boolean geoJson = useGeoJsonFeatures && !Placement.VALUE.equals(placement);

			jsonGen.writeStartObject();

			if (geoJson) {
				// feature intro

				// GeoJson type is feature
				jsonGen.writeStringField("type", "Feature");

				if (type != null) {
					String typePrefix = namespaces.getPrefix(type.getName().getNamespaceURI());
					if (!typePrefix.isEmpty()) {
						typePrefix = typePrefix + ':';
					}
					// type
					jsonGen.writeStringField("@type", typePrefix + type.getName().getLocalPart());
				}

				// TODO support use case of custom CRS (if root)? (not allowed
				// in GeoJson spec)

				// geometry
				GeometryProperty<?> geom = getSingleGeometry(instance);
				jsonGen.writeFieldName("geometry");
				if (geom != null && geom.getGeometry() != null) {
					writeGeometryValue(jsonGen, geom, log);
				}
				else {
					jsonGen.writeNull();
				}

				// start properties
				jsonGen.writeFieldName("properties");
				jsonGen.writeStartObject();
			}

			// check if the instance contains a value and write it down
			Object value = instance.getValue();
			if (value != null) {
				// XXX should the instance value use a namespace
				// writeFieldName(jsonGen, "value", NAMESPACE_INSTANCE_JSON);
				// XXX or instead @notation?
				jsonGen.writeFieldName("@value");
				writeValue(jsonGen, value, log);
			}

			// write all properties
			writeProperties(jsonGen, instance, log);

			if (geoJson) {
				// feature outro

				// end properties
				jsonGen.writeEndObject();

				// namespaces -> this is done at the end to be sure to have all
				// namespace prefixes
				if (Placement.ROOT.equals(placement)) {
					// only for root - expect namespaces in feature collection
					// for members

					writeNamespaces(jsonGen);
				}
			}

			jsonGen.writeEndObject();
		}
	}

	/**
	 * @param instance instance
	 * @return geometry
	 */
	protected GeometryProperty<?> getSingleGeometry(Instance instance) {
		List<GeometryProperty<? extends Geometry>> geoms = GeometryHelperFunctions
				._findAll(instance);

		return GeometryHelperFunctions._aggregate(geoms);
	}

	/**
	 * Write namespace information.
	 *
	 * @param jsonGen the Json generator
	 * @throws IOException if an error occurs writing the Json
	 */
	protected void writeNamespaces(JsonGenerator jsonGen) throws IOException {
		jsonGen.writeFieldName("@namespaces");
		jsonGen.writeStartObject();

		if (namespaces.getNamespaces() != null) {
			for (Entry<String, String> entry : namespaces.getNamespaces().entrySet()) {
				if (!entry.getKey().isEmpty() || !entry.getValue().isEmpty()) {
					jsonGen.writeStringField(entry.getKey(), entry.getValue());
				}
			}
		}

		jsonGen.writeEndObject();
	}

	/**
	 * Write a qualified field name.
	 *
	 * @param jsonGen the Json generator
	 * @param name the qualified name of the field
	 * @throws IOException if an error occurs writing the field name
	 */
	protected void writeFieldName(JsonGenerator jsonGen, QName name) throws IOException {
		writeFieldName(jsonGen, name.getLocalPart(), name.getNamespaceURI());
	}

	/**
	 * Write a qualified field name.
	 *
	 * @param jsonGen the Json generator
	 * @param name the local name of the field
	 * @param namespace the namespace of the field
	 * @throws IOException if an error occurs writing the field name
	 */
	protected void writeFieldName(JsonGenerator jsonGen, String name, String namespace)
			throws IOException {
		StringBuffer nameBuffer = new StringBuffer();
		if (namespace == null) {
			namespace = "";
		}
		String prefix = namespaces.getPrefix(namespace);
		if (!prefix.isEmpty()) {
			nameBuffer.append(prefix);
			nameBuffer.append(":");
		}
		nameBuffer.append(name);
		jsonGen.writeFieldName(nameBuffer.toString());
	}

	/**
	 * @param jsonGen json generator
	 * @param group group
	 * @param log logger
	 * @throws IOException exception
	 */
	protected void writeGroup(JsonGenerator jsonGen, Group group, SimpleLog log)
			throws IOException {
		jsonGen.writeStartObject();
		writeProperties(jsonGen, group, log);
		jsonGen.writeEndObject();
	}

	/**
	 * Handles skipping choice groups.
	 *
	 * @param propertyName the start property name
	 * @param obj the object to inspect
	 * @param log logger
	 * @return a pair of property name and value to use
	 */
	protected Pair<QName, Object> skipChoice(QName propertyName, Object obj, SimpleLog log) {
		if (obj instanceof Group) {
			Group group = (Group) obj;
			// For choices search for the (only!) child and skip the choice.
			if (group.getDefinition() instanceof GroupPropertyDefinition) {
				if (((GroupPropertyDefinition) group.getDefinition())
						.getConstraint(ChoiceFlag.class).isEnabled()) {
					Iterator<QName> childPropertyNames = group.getPropertyNames().iterator();

					if (!childPropertyNames.hasNext()) {
						log.warn("Found an empty choice.");
						return null;
					}

					QName childPropertyName = childPropertyNames.next();
					Object[] values = group.getProperty(childPropertyName);
					Object value = values[0];
					if (childPropertyNames.hasNext() || values.length > 1)
						log.warn("Found a choice with multiple children. Using first.");

					// delegate to only value
					return skipChoice(childPropertyName, value, log);
				}
			}
		}
		return new Pair<>(propertyName, obj);
	}

	/**
	 * Writes the properties of a group into JSON
	 * 
	 * @param jsonGen json generator
	 * @param group the group to write
	 * @param log logger
	 * @throws IOException if writing the file failed
	 */
	protected void writeProperties(JsonGenerator jsonGen, Group group, SimpleLog log)
			throws IOException {
		// FIXME is there the need to separate attributes and elements? (can
		// they overlap in definition?)

		// iterate over all properties
		Iterator<QName> nameIt = group.getPropertyNames().iterator();
		while (nameIt.hasNext()) {
			QName name = nameIt.next();
			Object[] values = group.getProperty(name);

			// determine if array should be used for property
			boolean useArray = values != null && values.length > 1;
			ChildDefinition<?> child = group.getDefinition().getChild(name);
			if (child != null) {
				useArray = DefinitionUtil.getCardinality(child).mayOccurMultipleTimes();
			}

			if (values != null && values.length > 0) {

				// replace choices with their content
				Multimap<QName, Object> valueMap = LinkedHashMultimap.create(1, values.length);
				for (Object value : values) {
					Pair<QName, Object> useValue = skipChoice(name, value, log);
					if (useValue != null)
						valueMap.put(useValue.getFirst(), useValue.getSecond());
				}

				for (QName propName : valueMap.keySet()) {
					Collection<Object> realValues = valueMap.get(propName);
					writeFieldName(jsonGen, propName);

					if (useArray) {
						jsonGen.writeStartArray();
						for (Object obj : realValues) {
							writeValue(jsonGen, obj, log);
						}
						jsonGen.writeEndArray();
					}
					else {
						// write single value
						if (realValues.isEmpty()) {
							jsonGen.writeNull();
						}
						else {
							writeValue(jsonGen, realValues.iterator().next(), log);
						}
					}
				}
			}
		}
	}

	/**
	 * Writes the properties of a value into JSON
	 * 
	 * @param jsonGen json generator
	 * @param value value to be written
	 * @param log logger
	 * @throws IOException exception
	 */
	public void writeValue(JsonGenerator jsonGen, Object value, SimpleLog log) throws IOException {
		if (value == null) {
			jsonGen.writeNull();
		}
		else if (value instanceof InstanceCollection) {
			writeCollection(jsonGen, (InstanceCollection) value, log);
		}
		else if (value instanceof Instance) {
			writeInstance(jsonGen, (Instance) value, Placement.VALUE, log);
		}
		else if (value instanceof Group) {
			writeGroup(jsonGen, (Group) value, log);
		}
		else {
			writeSimpleValue(jsonGen, value, log);
		}
	}

	/**
	 * Writes the simple value into JSON
	 * 
	 * @param jsonGen json generator
	 * @param value value to be written
	 * @param log logger
	 * @throws IOException exception in any case
	 */
	protected void writeSimpleValue(JsonGenerator jsonGen, Object value, SimpleLog log)
			throws IOException {
		// FIXME write an array based on the property type?

		// detect geometry collections (as occurring in XML schemas)
		// FIXME!!!!
		if (value instanceof Collection) {
			Collection<?> col = (Collection<?>) value;
			if (!col.isEmpty()) {
				Object element = col.iterator().next();
				if (element instanceof GeometryProperty<?> || element instanceof Geometry) {
					// extract geometry
					value = element;

					if (col.size() > 1) {
						// there are more geometry values
						// XXX can we handle multiple here?
						// XXX for now warn
						log.warn("Ignoring multiple geometries in instance value");
						// FIXME create geometry collection? / aggregate
					}
				}
			}
		}

		if (value instanceof Geometry) {
			writeGeometryValue(jsonGen, new DefaultGeometryProperty<>(null, (Geometry) value), log);
		}
		else if (value instanceof GeometryProperty<?>) {
			writeGeometryValue(jsonGen, (GeometryProperty<?>) value, log);
		}
		else if (value instanceof Number) {
			writeNumericValue(jsonGen, (Number) value, log);
		}
		else {
			// XXX use conversion service or something?
			jsonGen.writeString(value.toString());
		}
	}

	/**
	 * Writes the numeric values into JSON
	 * 
	 * @param jsonGen json generator
	 * @param num numeric value
	 * @param log logger
	 * @throws IOException exception in any case
	 */
	protected void writeNumericValue(JsonGenerator jsonGen, Number num, SimpleLog log)
			throws IOException {
		if (num == null) {
			jsonGen.writeNull();
		}
		else if (num instanceof Integer) {
			jsonGen.writeNumber((Integer) num);
		}
		else if (num instanceof Float) {
			jsonGen.writeNumber((Float) num);
		}
		else if (num instanceof Double) {
			jsonGen.writeNumber((Double) num);
		}
		else if (num instanceof Long) {
			jsonGen.writeNumber((Long) num);
		}
		else if (num instanceof BigDecimal) {
			jsonGen.writeNumber((BigDecimal) num);
		}
		else if (num instanceof BigInteger) {
			jsonGen.writeNumber((BigInteger) num);
		}
		else {
			// not sure how to handle this
			// trying string representation
			jsonGen.writeNumber(String.valueOf(num));
			log.warn(
					"Encountered unsupported number type {0}, using its String representation in Json which may result in invalid Json",
					num.getClass());
		}
	}

	/**
	 * Writes geometry properties into JSON
	 * 
	 * @param jsonGen json generator
	 * @param geomProp geometry property
	 * @param log logger
	 * @throws IOException exception
	 */
	protected void writeGeometryValue(JsonGenerator jsonGen, GeometryProperty<?> geomProp,
			SimpleLog log) throws IOException {
		Geometry geom = geomProp.getGeometry();
		if (geom == null) {
			jsonGen.writeNull();
			return;
		}

		@SuppressWarnings("unused")
		boolean invalidGeom = false;
		if (targetCrs != null) {
			if (geomProp.getCRSDefinition() != null) {
				try {
					MathTransform transform = transformCache
							.getTransform(geomProp.getCRSDefinition().getCRS(), targetCrs.getCRS());
					geom = JTS.transform(geom, transform);
				} catch (Exception e) {
					invalidGeom = true;
					log.error("Could not transform geometry to target CRS", e);
				}
			}
			else {
				invalidGeom = true;
				log.warn("No CRS known for geometry, cannot convert to target CRS");
			}
		}

		// correct winding order as per right-hand rule, i.e.,
		// exterior rings are counterclockwise, and holes are
		// clockwise.
		geom = WindingOrder.unifyWindingOrder(geomProp.getGeometry(), true,
				geomProp.getCRSDefinition().getCRS());

		// FIXME what to do in case of an invalid geometry?

		jsonGen.writeRawValue(geometryJson.toString(geom));
	}

	/**
	 * Method to generate json from instance.
	 * 
	 * @param instance instance
	 * @param log reporter
	 * @return json in string format
	 * @throws IOException exception
	 */
	public String toJsonString(Instance instance, SimpleLog log) throws IOException {
		StringWriter writer = new StringWriter();
		InstanceToJson.<Void> withJsonGenerator(writer, true, json -> {
			try {
				writeInstance(json, instance, log);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			return null;
		});
		writer.close();
		return writer.toString();
	}

	/**
	 * Method to generate json from instanceCollection.
	 * 
	 * @param instanceCollection instance collection
	 * @param log reporter
	 * @return json in string format
	 * @throws IOException exception
	 */
	public String toJsonString(InstanceCollection instanceCollection, SimpleLog log)
			throws IOException {

		StringWriter writer = new StringWriter();
		InstanceToJson.<Void> withJsonGenerator(writer, true, json -> {
			try {
				writeCollection(json, instanceCollection, log);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			return null;
		});
		writer.close();
		return writer.toString();
	}
}
